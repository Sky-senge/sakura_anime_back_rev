package com.computerapplicationtechnologycnus.sakura_anime.model.webRequestModel;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class UserModPasswordRequestModel {
    @Schema(description = "请求需要更改的用户ID")
    Long id;
    @Schema(description = "请求需要更改的用户密码")
    String password;
}
