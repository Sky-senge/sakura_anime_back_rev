package com.computerapplicationtechnologycnus.sakura_anime.model.webRequestModel;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


@Data
public class HistoryRequestModel {
    @Schema(description = "对应用户ID")
    private Long userId;

    @Schema(description = "对应动漫ID")
    private Long animeId;

    @Schema(description = "动漫集数")
    private Long episodes;
}
