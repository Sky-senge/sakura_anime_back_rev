package com.computerapplicationtechnologycnus.sakura_anime.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class History {
    @Schema(description = "评论唯一ID")
    private Long id;

    @Schema(description = "对应用户ID")
    private Long userId;

    @Schema(description = "对应动漫ID")
    private Long animeId;

    @Schema(description = "动漫集数")
    private Long episodes;

    @Schema(description = "评论创建时间")
    private Timestamp createAt;
}
