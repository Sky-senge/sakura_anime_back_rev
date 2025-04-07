package com.computerapplicationtechnologycnus.sakura_anime.services;

import com.alibaba.fastjson.JSON;
import com.computerapplicationtechnologycnus.sakura_anime.config.FFmpegConfig;
import com.computerapplicationtechnologycnus.sakura_anime.controller.FileController;
import com.computerapplicationtechnologycnus.sakura_anime.config.FileStorageProperties;
import com.computerapplicationtechnologycnus.sakura_anime.model.webRequestModel.AnimeResponseModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static com.computerapplicationtechnologycnus.sakura_anime.controller.FileController.FFmpeg_COMMAND;

@Service
public class VideoService {

    private final ExecutorService executorService = Executors.newFixedThreadPool(4); // 线程池
    private static final Logger logger = LoggerFactory.getLogger(VideoService.class);

    private final FFmpegConfig ffmpegConfig;
    private final FileStorageProperties fileStorageProperties;

    @Autowired
    public VideoService(FFmpegConfig ffmpegConfig,FileStorageProperties fileStorageProperties){
        this.ffmpegConfig=ffmpegConfig;
        this.fileStorageProperties=fileStorageProperties;
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
                Integer ffmpegThreads = ffmpegConfig.getThreads();
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
                        ffmpegLocate+" -threads "+ffmpegThreads+" -hwaccel auto -i %s -c:v "+encodingType+" -b:v "+ffmpegConfig.getVideo().getAvgrate()+"k -maxrate "+ffmpegConfig.getVideo().getMaxrate()+"k -bufsize 10000k " +
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
                Integer ffmpegThreads = ffmpegConfig.getThreads();
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
                        ffmpegLocate + " -threads "+ffmpegThreads+" -hwaccel auto -i %s -vf subtitles='%s' " +
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

    /**
     * 检测 videoFileSequence 是否包含非法字符
     * @param videoFileSequence 视频文件的唯一路径标识符
     * @return 如果合法返回 true，否则返回 false
     */
    public boolean isValidVideoFileSequence(String videoFileSequence) {
        // 检查是否为空或空字符串
        if (videoFileSequence == null || videoFileSequence.trim().isEmpty()) {
            return false;
        }

        // 定义非法字符的正则表达式
        // 包含路径穿越符号 ".." 或其他非法字符（如 / \ : * ? " < > |）
        Pattern illegalPattern = Pattern.compile(".*(\\.\\.|[/\\\\:*?\"<>|]).*");

        // 检查是否匹配非法字符
        if (illegalPattern.matcher(videoFileSequence).matches()) {
            return false;
        }

        // 如果通过所有检查，返回 true
        return true;
    }

    /**
     * 删除对应序列的视频文件
     * 【警告】此操作不可逆！
     * @param videoFileSequence 视频文件的唯一路径标识符
     */
    public void deleteVideoFile(String videoFileSequence) {
        // 检查videoFileSequence是否合规
        if(!isValidVideoFileSequence(videoFileSequence)){
            throw new IllegalArgumentException("非法路径标识符：" + videoFileSequence);
        }
        // 构建完整的文件路径
        String videoFilePath = fileStorageProperties.getUploadDir() + "anime/" + videoFileSequence;
        Path filePath = Paths.get(videoFilePath);

        logger.warn("即将删除视频资源：" + videoFilePath);

        try {
            // 检查路径是否存在
            if (Files.exists(filePath)) {
                // 如果是目录，递归删除目录及其内容
                if (Files.isDirectory(filePath)) {
                    try (Stream<Path> paths = Files.walk(filePath)) {
                        paths.sorted(Comparator.reverseOrder()) // 先删除子文件和子目录
                                .forEach(path -> {
                                    try {
                                        Files.delete(path);
                                        logger.info("删除成功：" + path);
                                    } catch (IOException e) {
                                        logger.error("删除失败：" + path, e);
                                        throw new RuntimeException("删除失败：" + path, e);
                                    }
                                });
                    }
                } else {
                    // 如果是文件，直接删除
                    Files.delete(filePath);
                    logger.info("视频资源删除成功：" + videoFilePath);
                }
            } else {
                logger.info("视频资源不存在，无需删除：" + videoFilePath);
            }
        } catch (IOException e) {
            logger.error("删除视频资源失败：" + videoFilePath, e);
            throw new RuntimeException("删除视频资源失败：" + e.getMessage(), e);
        }
    }

    /**
     * 重命名视频文件夹
     * @param oldFileName 原文件夹名称
     * @param newFileName 新文件夹名称
     * @return 操作是否成功
     */
    public boolean renameVideoFolder(String oldFileName, String newFileName) {
        // 检查文件名是否合规
        if(!isValidVideoFileSequence(oldFileName) || !isValidVideoFileSequence(newFileName)){
            throw new IllegalArgumentException("非法路径标识符：" +
                    (!isValidVideoFileSequence(oldFileName) ? oldFileName : newFileName));
        }

        // 构建完整路径
        String basePath = fileStorageProperties.getUploadDir() + "anime/";
        Path oldPath = Paths.get(basePath + oldFileName);
        Path newPath = Paths.get(basePath + newFileName);

        logger.info("即将重命名文件夹：从 " + oldFileName + " 到 " + newFileName);

        try {
            // 检查原路径是否存在
            if (Files.exists(oldPath)) {
                // 检查新路径是否已存在
                if (Files.exists(newPath)) {
                    logger.error("目标文件夹已存在：" + newPath);
                    return false;
                }

                // 执行重命名操作
                Files.move(oldPath, newPath);
                logger.info("文件夹重命名成功：" + oldFileName + " -> " + newFileName);
                return true;
            } else {
                logger.error("原文件夹不存在：" + oldPath);
                return false;
            }
        } catch (IOException e) {
            logger.error("重命名文件夹失败：" + oldPath + " -> " + newPath, e);
            throw new RuntimeException("重命名文件夹失败：" + e.getMessage(), e);
        }
    }

    /**
     * 下架视频时不会主动删除资源，但会备份对应的集数Index数据。
     * @param animeDetails 即将下架的动漫资源详情
     */
    public void backupVideoIndex(AnimeResponseModel animeDetails) {
        String animeBasePath = fileStorageProperties.getUploadDir() + "anime/";
        String fileName;

        // 尝试生成 "id_动漫名称_IndexData.json" 文件名
        String animeName = animeDetails.getName();
        if (animeName != null && !animeName.trim().isEmpty()) {
            // 仅将空格替换为下划线
            String safeAnimeName = animeName.replace(" ", "_");
            fileName = animeDetails.getId() + "_" + safeAnimeName + "_IndexData.json";
        } else {
            // 如果动漫名称为空，使用 "id_当前时间_IndexData.json" 文件名
            String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
            fileName = animeDetails.getId() + "_" + timeStamp + "_IndexData.json";
            logger.warn("无法生成带动漫名称的文件，使用时间戳文件名：" + fileName);
        }

        // 构建完整文件路径
        String filePath = animeBasePath + fileName;

        // 将 animeDetails 对象转换为 JSON 并写入文件
        try {
            // 使用 fastjson 将对象转换为 JSON 字符串
            String jsonString = JSON.toJSONString(animeDetails, true); // true 表示格式化输出
            File file = new File(filePath);

            // 检查文件是否可创建
            if (file.createNewFile()) {
                // 写入 JSON 数据
                Files.write(file.toPath(), jsonString.getBytes());
                logger.info("动漫索引数据备份成功，文件路径：" + filePath);
            } else {
                // 如果文件无法创建，使用时间戳文件名重试
                String fallbackFileName = animeDetails.getId() + "_" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + "_IndexData.json";
                String fallbackFilePath = animeBasePath + fallbackFileName;
                File fallbackFile = new File(fallbackFilePath);

                if (fallbackFile.createNewFile()) {
                    Files.write(fallbackFile.toPath(), jsonString.getBytes());
                    logger.warn("原文件名无法创建，已使用备用文件名：" + fallbackFilePath);
                } else {
                    throw new IOException("无法创建文件：" + fallbackFilePath);
                }
            }
        } catch (IOException e) {
            logger.error("动漫索引数据备份失败：" + e.getMessage(), e);
            throw new RuntimeException("动漫索引数据备份失败：" + e.getMessage(), e);
        }
    }
}
