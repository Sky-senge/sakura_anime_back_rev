package com.computerapplicationtechnologycnus.sakura_anime.model;

import lombok.Data;

@Data
public class LastUpdate {
    private Long id;                // 主键，唯一索引，一般永远为1
    private String videoLastUpdate; // 视频最后更新时间
    private String userLastUpdate;  // 用户最后更新时间
    private String commentLastUpdate; // 评论最后更新时间
}