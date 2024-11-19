package com.computerapplicationtechnologycnus.sakura_anime.model.webRequestModel;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class UserRegistryModel {

    @Schema(description = "唯一用户邮箱")
    private String email;

    @Schema(description = "唯一用户名")
    private String username;

    @Schema(description = "权限级别[0:管理员，1:普通用户,2:已被封锁]")
    private Integer permission;

    @Schema(description = "加密后密码")
    private String password;

    @Schema(description = "显示名称，可改可重复")
    private String displayName;

    @Schema(description = "个性签名")
    private String remarks;
}
