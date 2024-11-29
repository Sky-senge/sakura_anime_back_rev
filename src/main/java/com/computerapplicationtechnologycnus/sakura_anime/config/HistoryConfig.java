package com.computerapplicationtechnologycnus.sakura_anime.config;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "history")
public class HistoryConfig {
    @Schema(description = "单个用户能存储的最大历史记录条数")
    private int maxSingleUser = 300; //默认值
}
