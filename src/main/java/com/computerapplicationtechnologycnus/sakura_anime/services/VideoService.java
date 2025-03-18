package com.computerapplicationtechnologycnus.sakura_anime.services;

import com.computerapplicationtechnologycnus.sakura_anime.config.FFmpegConfig;
import com.computerapplicationtechnologycnus.sakura_anime.controller.FileController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.computerapplicationtechnologycnus.sakura_anime.controller.FileController.FFmpeg_COMMAND;

@Service
public class VideoService {

    private final ExecutorService executorService = Executors.newFixedThreadPool(4); // 线程池
    private static final Logger logger = LoggerFactory.getLogger(VideoService.class);

    private final FFmpegConfig ffmpegConfig;

    @Autowired
    public VideoService(FFmpegConfig FFmpegConfig){
        this.ffmpegConfig=FFmpegConfig;
    }

    /**
     * 异步转码视频到 m3u8
     * 如果视频内嵌了中文字幕(合规嵌入的ass,str文件) 那么也会一并拉出来渲染
     *
     * @param videoFilePath 原视频文件路径
     */
    public void convertVideoToM3u8(String videoFilePath) {
        executorService.submit(() -> {
            try {

                // 创建 m3u8 输出文件夹
                String outputDirPath = videoFilePath.substring(0, videoFilePath.lastIndexOf('.'));
                File outputDir = new File(outputDirPath);
                if (!outputDir.exists() && !outputDir.mkdirs()) {
                    throw new IOException("无法创建输出目录：" + outputDirPath);
                }

                // 执行 ffmpeg 命令，查看是否存在字幕轨道
                String checkCommand = "ffmpeg -i " + videoFilePath;
                Process checkProcess = Runtime.getRuntime().exec(checkCommand);
                BufferedReader checkReader = new BufferedReader(new InputStreamReader(checkProcess.getErrorStream()));
                String checkLine;
                int subtitleStreamIndex = -1; // 用于存储找到的字幕流索引
                String subtitleTrackLanguage = ""; // 用来存储找到的字幕语言
                String subtitleTrackType = ""; //字幕类型
                String subtitleFilePath = ""; //字幕文件路径
                Pattern pattern = Pattern.compile(".*subtitle.*", Pattern.CASE_INSENSITIVE); // 匹配所有包含 'subtitle' 的行，不区分大小写

                // 定义需要匹配的语言标志
                List<String> languageIndicators = Arrays.asList("sc", "zh", "chs", "zho", "chi");

                while ((checkLine = checkReader.readLine()) != null) {
                    // 查找字幕流的描述信息
                    if (checkLine.contains("Subtitle")) {
                        logger.info("原始字幕行: {}", checkLine);
                        // 调整正则表达式，并打印详细日志
                        Pattern subtitlePattern = Pattern.compile("Stream #(\\d+:\\d+)\\s*\\(([^)]+)\\):\\s*Subtitle:\\s*(.+)");
                        Matcher streamMatcher = subtitlePattern.matcher(checkLine);
                        logger.info("正则表达式: {}", subtitlePattern.pattern());
                        logger.info("待匹配文本: {}", checkLine);
                        if (streamMatcher.find()) {
                            String streamId = streamMatcher.group(1);
                            String language = streamMatcher.group(2).toLowerCase();
                            String subtitleType = streamMatcher.group(3).split(" ")[0];
                            logger.info("匹配成功 - 流ID: {}, 语言: {}, 字幕类型: {}", streamId, language, subtitleType);
                            if (languageIndicators.contains(language)) {
                                // 执行命令行赋值
                                subtitleStreamIndex = Integer.parseInt(streamId.split(":")[1]);
                                subtitleTrackLanguage = language;
                                subtitleTrackType = subtitleType;
                                logger.info("匹配的语言符合条件，已设置字幕流参数");
                                break; // 退出循环，因为已经找到符合条件的字幕流
                            }
                        } else {
//                            logger.warn("未能匹配字幕行: {}", checkLine);
                            logger.info("未能匹配字幕行，可能不存在字幕");
                        }
                    }
                }

                // 构建 FFmpeg 命令
                String m3u8FilePath = outputDirPath + "/playlist.m3u8";
                String encodingType = "libx264";
                String subtitleTransfer = "";
                String ffmpegLocate = ffmpegConfig.getLocate();
                if(ffmpegLocate.isEmpty()){ //防止空命令错误，如果为空，默认使用"ffmpeg"命令
                    ffmpegLocate="ffmpeg";
                }
                if(ffmpegConfig.getVideo().isEnableNvenc()){ //如果启用NVENC加速
                    encodingType = "h264_nvenc";
                }
                // 如果找到字幕流，先把字幕拉出来，然后再合并烧录
                if (subtitleStreamIndex != -1) {
                    // 构建字幕文件路径（去掉原视频文件扩展名，添加字幕扩展名）
                    subtitleFilePath = videoFilePath.substring(0, videoFilePath.lastIndexOf('.')) + "." + subtitleTrackType;

                    // 构建 FFmpeg 提取字幕的命令
                    String extractSubtitleCommand = String.format(
                            ffmpegLocate+" -i \"%s\" -map 0:%d \"%s\"",
                            videoFilePath,
                            subtitleStreamIndex,
                            subtitleFilePath
                    );
                    try {
                        // 执行提取字幕的命令
                        Process extractProcess = Runtime.getRuntime().exec(extractSubtitleCommand);
                        // 处理输出流和错误流
                        Thread outputReader = new Thread(() -> {
                            try (BufferedReader reader = new BufferedReader(new InputStreamReader(extractProcess.getInputStream()))) {
                                String line;
                                while ((line = reader.readLine()) != null) {
                                    logger.info("FFmpeg 输出: {}", line);
                                }
                            } catch (IOException e) {
                                logger.error("读取 FFmpeg 输出时发生错误", e);
                            }
                        });

                        Thread errorReader = new Thread(() -> {
                            try (BufferedReader reader = new BufferedReader(new InputStreamReader(extractProcess.getErrorStream()))) {
                                String line;
                                while ((line = reader.readLine()) != null) {
                                    logger.info("FFmpeg 预处理: {}", line);
                                }
                            } catch (IOException e) {
                                logger.error("读取 FFmpeg 错误时发生错误", e);
                            }
                        });

                        // 启动线程
                        outputReader.start();
                        errorReader.start();
                        // 等待进程结束
                        int extractExitCode = extractProcess.waitFor();
                        // 确保线程完成
                        outputReader.join();
                        errorReader.join();

                        if (extractExitCode == 0) {
                            // 提取成功
                            logger.info("字幕提取成功，路径: {}", subtitleFilePath);

                            // 构建字幕烧录的参数
                            subtitleTransfer = String.format(
                                    "-vf subtitles='%s'",
                                    subtitleFilePath.replace("\\\\","/").replace(":", "\\:")
                            );
                        } else {
                            // 提取失败
                            logger.error("字幕提取失败，退出码: {}", extractExitCode);
                            subtitleFilePath = ""; // 重置字幕文件路径
                            subtitleTransfer = ""; //重置字幕命令部分
                        }
                    } catch (Exception e) {
                        logger.error("提取字幕时发生错误", e);
                        subtitleFilePath = ""; // 重置字幕文件路径
                        subtitleTransfer = ""; //重置字幕命令部分
                    }
                }
                //最终执行命令Builder
                String command = String.format(
                        ffmpegLocate+" -threads 0 -hwaccel auto -i %s -c:v "+encodingType+" -b:v "+ffmpegConfig.getVideo().getAvgrate()+"k -maxrate "+ffmpegConfig.getVideo().getMaxrate()+"k -bufsize 10000k " +
                                "-profile:v high -level 5.1 -map v:0 -map a:0 "+subtitleTransfer+" -c:a aac -ar 48k -ac 2 -b:a 256k " +
                                "-pix_fmt yuv420p -sws_flags lanczos -f hls -hls_time "+ffmpegConfig.getHls().getTime()+" -hls_list_size 0 %s",
                        videoFilePath, m3u8FilePath
                );

                // 执行 FFmpeg 命令
                logger.info("执行 FFmpeg 转码命令: {}", command);
                Process process = Runtime.getRuntime().exec(command);

                // 处理标准输出和错误流
                new Thread(() -> {
                    try (var reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            logger.info("FFmpeg 输出: {}", line);
                        }
                    } catch (IOException e) {
                        logger.error("读取 FFmpeg 标准输出时发生错误", e);
                    }
                }).start();

                new Thread(() -> {
                    try (var reader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            logger.info("FFmpeg 信息: {}", line);
                        }
                    } catch (IOException e) {
                        logger.error("读取 FFmpeg 错误输出时发生错误", e);
                    }
                }).start();

                int exitCode = process.waitFor();
                if (exitCode == 0) {
                    logger.info("转码成功，输出路径: {}", m3u8FilePath);
                } else {
                    logger.error("转码失败，FFmpeg 退出码: {}", exitCode);
                }

                // 转码完成后，删除原视频文件
                File originalFile = new File(videoFilePath);
                if (originalFile.exists() && !originalFile.delete()) {
                    logger.warn("无法删除原视频文件: {}", videoFilePath);
                }
                if(subtitleStreamIndex!=-1){
                    File originalSubFile = new File(subtitleFilePath);
                    if(originalSubFile.exists() && !originalSubFile.delete()){
                        logger.warn("无法删除生成的字幕文件：{}",subtitleFilePath);
                    }
                }

            } catch (Exception e) {
                logger.error("视频转码过程中发生错误", e);
            }
        });
    }

    private String extractLanguageFromSubtitleLine(String line) {
        // 使用正则表达式从字幕信息中提取语言
        // 例如从 "Stream #0:3(chi): Subtitle: ass (default)" 中提取 "chi"
        Pattern languagePattern = Pattern.compile("Stream.*\\((.*?)\\): Subtitle", Pattern.CASE_INSENSITIVE);
        Matcher matcher = languagePattern.matcher(line);

        if (matcher.find()) {
            return matcher.group(1); // 返回字幕轨道的语言部分
        }
        return "";
    }

    /**
     * 异步转码视频到 m3u8
     * 并烧录字幕文件
     *
     * @param videoFilePath 原视频文件路径
     */
    public void convertVideoToM3u8AddSubtitle(String videoFilePath,String subtitlePath) {
        executorService.submit(() -> {
            try {

                // 创建 m3u8 输出文件夹
                String outputDirPath = videoFilePath.substring(0, videoFilePath.lastIndexOf('.'));
                File outputDir = new File(outputDirPath);
                if (!outputDir.exists() && !outputDir.mkdirs()) {
                    throw new IOException("无法创建输出目录：" + outputDirPath);
                }

                // 构建 FFmpeg 命令
                String m3u8FilePath = outputDirPath + "/playlist.m3u8";
                String encodingType = "libx264";
                String ffmpegLocate = ffmpegConfig.getLocate();
                if(ffmpegLocate.isEmpty()){ //防止空命令错误，如果为空，默认使用"ffmpeg"命令
                    ffmpegLocate="ffmpeg";
                }
                if(ffmpegConfig.getVideo().isEnableNvenc()){ //如果启用NVENC加速
                    encodingType = "h264_nvenc";
                }
                //刁钻的ffmpeg字幕转义字符
                String ffmpegSubtitlePath = subtitlePath.replace(":", "\\:").replace("/", "\\\\");

                String command = String.format(
                        ffmpegLocate + " -threads 0 -hwaccel auto -i %s -vf subtitles='%s' " +
                                "-c:v " + encodingType + " -b:v " + ffmpegConfig.getVideo().getAvgrate() + "k -maxrate " + ffmpegConfig.getVideo().getMaxrate() + "k -bufsize 10000k " +
                                "-profile:v high -level 5.1 -map v:0 -map a:0 -c:a aac -ar 48k -ac 2 -b:a 256k " +
                                "-pix_fmt yuv420p -sws_flags lanczos -f hls -hls_time " + ffmpegConfig.getHls().getTime() + " -hls_list_size 0 %s",
                        videoFilePath, ffmpegSubtitlePath, m3u8FilePath
                );

                // 执行 FFmpeg 命令
                logger.info("执行FFmpeg转码烧录字幕命令: {}", command);
                Process process = Runtime.getRuntime().exec(command);

                // 处理标准输出和错误流
                new Thread(() -> {
                    try (var reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            logger.info("FFmpeg 输出: {}", line);
                        }
                    } catch (IOException e) {
                        logger.error("读取 FFmpeg 标准输出时发生错误", e);
                    }
                }).start();

                new Thread(() -> {
                    try (var reader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            logger.info("FFmpeg 信息: {}", line);
                        }
                    } catch (IOException e) {
                        logger.error("读取 FFmpeg 错误输出时发生错误", e);
                    }
                }).start();

                int exitCode = process.waitFor();
                if (exitCode == 0) {
                    logger.info("转码成功，输出路径: {}", m3u8FilePath);
                } else {
                    logger.error("转码失败，FFmpeg 退出码: {}", exitCode);
                }

                // 删除原视频文件
                File originalFile = new File(videoFilePath);
                File orignalSubFile = new File(subtitlePath);
                if (originalFile.exists() && !originalFile.delete()) {
                    logger.warn("无法删除原视频文件: {}", videoFilePath);
                }
                if (orignalSubFile.exists() && !orignalSubFile.delete()) {
                    logger.warn("无法删除原字幕文件: {}", videoFilePath);
                }

            } catch (Exception e) {
                logger.error("视频转码过程中发生错误", e);
            }
        });
    }
}
