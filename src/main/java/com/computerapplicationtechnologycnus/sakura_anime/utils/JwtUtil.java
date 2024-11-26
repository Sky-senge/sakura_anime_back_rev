package com.computerapplicationtechnologycnus.sakura_anime.utils;

import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * JWT 工具类，用于生成和解析 JWT。
 */
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;  // 私钥

    @Value("${jwt.expiration}")
    private Long expiration;  // 过期时间，单位毫秒

    // 生成 JWT Token
    public String generateToken(Long userId, String username, int permission,String encrypted_password) {
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("username", username)
                .claim("permission", permission)
                .claim("passkey",encrypted_password)
                .setExpiration(new Date(System.currentTimeMillis() + expiration))  // 设置过期时间
                .signWith(SignatureAlgorithm.HS512, secret)  // 使用 HMAC-SHA-512 签名
                .compact();
    }

    // 从 Token 中获取 Claims（包括用户信息）
    public Claims getClaimsFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();
    }

    // 验证 Token 是否过期
    public boolean isTokenExpired(String token) {
        return getClaimsFromToken(token).getExpiration().before(new Date());
    }
}
