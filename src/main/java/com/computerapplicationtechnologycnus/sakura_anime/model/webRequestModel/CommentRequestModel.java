package com.computerapplicationtechnologycnus.sakura_anime.model.webRequestModel;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class CommentRequestModel {

    @Schema(description = "对应动漫ID")
    private Long animeId;

    @Schema(description = "对应用户ID")
    private Long userId;

    @Schema(description = "评论内容")
    private String content;

    @Schema(description = "创建日期，SQL会自己生成，无需插入")
    private Timestamp createAt;
}
