package com.computerapplicationtechnologycnus.sakura_anime.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class AnimePathObject {
    @Schema(description = "动漫集数，剧场版直接写1，只有一集")
    private Long episodes;
    @Schema(description = "实际文件路径名")
    private String fileName;
}
