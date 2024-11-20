package com.computerapplicationtechnologycnus.sakura_anime.model.webRequestModel;

import com.alibaba.fastjson.JSON;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class AnimeCreateModel {

    @Schema(description = "动漫名称")
    private String name;

    @Schema(description = "标签，存储为JSON格式")
    private List<String> tags;

    @Schema(description = "动漫简介")
    private String description;

    @Schema(description = "评分，范围从1到10")
    private Float rating;

    @Schema(description = "视频文件路径")
    private String filePath;
}
