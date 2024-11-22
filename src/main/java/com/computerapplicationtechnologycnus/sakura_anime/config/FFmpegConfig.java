package com.computerapplicationtechnologycnus.sakura_anime.config;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

@Data
@Component
@Schema(description = "FFMPEG 配置")
public class FFmpegConfig {

    @Value("${ffmpeg.hls.time}")
    @Schema(description = "FFMPEG 单个切片时长")
    private int hlsTime;

    @Value("${ffmpeg.video.maxrate}")
    @Schema(description = "FFMPEG 最大码率")
    private int maxVideoRate;

    @Value("${ffmpeg.video.avgrate}")
    @Schema(description = "FFMPEG 平均码率")
    private int aveVideoRate;
}
