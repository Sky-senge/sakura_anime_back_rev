package com.computerapplicationtechnologycnus.sakura_anime.model.webRequestModel;

import lombok.Data;

@Data
public class UserLoginResponse {
    private String token;
    private Long userId;

    public UserLoginResponse(String token, Long userId) {
        this.token = token;
        this.userId = userId;
    }
}
