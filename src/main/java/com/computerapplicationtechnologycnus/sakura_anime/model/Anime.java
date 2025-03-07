package com.computerapplicationtechnologycnus.sakura_anime.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import com.alibaba.fastjson.JSON;

import java.sql.Date;
import java.util.List;

@Data
public class Anime {
    @Schema(description = "默认ID")
    private Long id;

    @Schema(description = "动漫名称")
    private String name;

    @Schema(description = "标签，存储为JSON格式")
    private String tags;

    @Schema(description = "动漫简介")
    private String description;

    @Schema(description = "评分，范围从1到10")
    private Float rating;

    @Schema(description = "发布日期")
    private Date releaseDate;

    @Schema(description = "视频文件路径")
    private String filePath;

    /**
     * 获取标签列表
     *
     * @return 标签的 List<String> 格式
     */
    public List<String> getTagsList() {
        return tags == null ? null : JSON.parseArray(tags, String.class);
    }

    /**
     * 设置标签列表
     *
     * @param tagsList 标签的 List<String> 格式
     */
    public void setTagsList(List<String> tagsList) {
        this.tags = tagsList == null ? null : JSON.toJSONString(tagsList);
    }
}
