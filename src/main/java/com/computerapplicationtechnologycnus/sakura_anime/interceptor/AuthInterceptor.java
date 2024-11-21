package com.computerapplicationtechnologycnus.sakura_anime.interceptor;

import com.computerapplicationtechnologycnus.sakura_anime.annotation.AuthRequired;
import com.computerapplicationtechnologycnus.sakura_anime.exception.AuthenticationException;
import com.computerapplicationtechnologycnus.sakura_anime.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(AuthInterceptor.class);

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        logger.info("AuthInterceptor triggered for URL: " + request.getRequestURI());

        // 如果不是处理方法直接放行
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        // 获取方法上的 @AuthRequired 注解
        AuthRequired authRequired = handlerMethod.getMethodAnnotation(AuthRequired.class);

        // 如果方法没有标注 @AuthRequired 注解，跳过认证
        if (authRequired == null) {
            logger.info("No @AuthRequired annotation found, skipping authorization.");
            return true;
        }

        int minPermissionLevel = authRequired.minPermissionLevel();
        logger.info("Required minPermissionLevel: " + minPermissionLevel);

        // 获取请求中的 token
        String token = request.getHeader("Authorization");
        if (token == null || token.isEmpty()) {
            logger.error("Missing token in request.");
            throw new AuthenticationException("Missing token");
        }

        try {
            // 提取 token（去掉 Bearer 前缀）
//            String jwtToken = token.startsWith("Bearer ") ? token.substring(7) : token;
            //无需提取
              String jwtToken = token;
            if (jwtUtil.isTokenExpired(jwtToken)) {
                logger.error("Token has expired.");
                throw new AuthenticationException("Token has expired");
            }

            // 验证Token有效性，并获取相关信息
            Claims claims = jwtUtil.getClaimsFromToken(jwtToken);
//            logger.info(String.valueOf(claims)); //调试，查看Token讯息
            Integer userPermissionLevel = claims.get("permission", Integer.class);

            // 权限验证
            if (userPermissionLevel == null || userPermissionLevel > minPermissionLevel) {
                logger.error("User does not have sufficient permissions. Required: {}, Found: {}.",
                        minPermissionLevel, userPermissionLevel);
                throw new AuthenticationException("Insufficient permissions");
            }

            logger.info("Token is valid and permissions are sufficient.");
            return true;

        } catch (JwtException e) {
            logger.error("Invalid token: " + e.getMessage());
            throw new AuthenticationException("Invalid token: " + e.getMessage());
        }
    }
}