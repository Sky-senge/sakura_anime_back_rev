package com.computerapplicationtechnologycnus.sakura_anime.model.webRequestModel;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class UserLoginRequest {
    @Schema(description = "唯一用户名")
    private String username;

    @Schema(description = "加密后密码")
    private String password;
}
