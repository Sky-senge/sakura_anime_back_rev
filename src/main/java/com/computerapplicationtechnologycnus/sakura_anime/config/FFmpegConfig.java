package com.computerapplicationtechnologycnus.sakura_anime.config;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "ffmpeg")
@Schema(description = "FFMPEG 配置")
public class FFmpegConfig {

    private Hls hls = new Hls();
    private Video video = new Video();

    @Data
    public static class Hls {
        @Schema(description = "FFMPEG 单个切片时长")
        private int time;
    }

    @Data
    public static class Video {
        @Schema(description = "FFMPEG 最大码率")
        private int maxrate;

        @Schema(description = "FFMPEG 平均码率")
        private int avgrate;

        @Schema(description = "是否开启NVENC，N卡转码加速")
        private boolean enableNvenc;
    }
}