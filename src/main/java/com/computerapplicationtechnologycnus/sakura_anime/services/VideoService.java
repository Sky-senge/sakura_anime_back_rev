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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.computerapplicationtechnologycnus.sakura_anime.controller.FileController.FFmpeg_COMMAND;

@Service
public class VideoService {

    private final ExecutorService executorService = Executors.newFixedThreadPool(4); // 线程池
    private static final Logger logger = LoggerFactory.getLogger(VideoService.class);

    private final FFmpegConfig ffmpegConfig;

    @Autowired
    public VideoService(FFmpegConfig fFmpegConfig){
        this.ffmpegConfig=fFmpegConfig;
    }

    /**
     * 异步转码视频到 m3u8
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

                // 构建 FFmpeg 命令
                String m3u8FilePath = outputDirPath + "/playlist.m3u8";
                String command = String.format(
                        "ffmpeg -i %s -c:v h264_nvenc -b:v "+ffmpegConfig.getAveVideoRate()+"k -maxrate "+ffmpegConfig.getMaxVideoRate()+"k -bufsize 10000k " +
                                "-profile:v high -level 5.1 -map v:0 -c:a aac -ar 48k -b:a 256k " +
                                "-pix_fmt yuv420p -sws_flags lanczos -f hls -hls_time "+ffmpegConfig.getHlsTime()+" -hls_list_size 0 %s",
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

                // 删除原视频文件
                File originalFile = new File(videoFilePath);
                if (originalFile.exists() && !originalFile.delete()) {
                    logger.warn("无法删除原视频文件: {}", videoFilePath);
                }

            } catch (Exception e) {
                logger.error("视频转码过程中发生错误", e);
            }
        });
    }
}
