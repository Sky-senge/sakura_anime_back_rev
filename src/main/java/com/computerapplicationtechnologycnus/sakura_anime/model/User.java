package com.computerapplicationtechnologycnus.sakura_anime.model;

import lombok.Data;

@Data
public class User {
    private Long id;
    private String avatar;
    private String email;
    private String username;
    private Integer premission;
    private String password;
    private String displayName;
    private String remarks;
}
