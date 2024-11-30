package com.computerapplicationtechnologycnus.sakura_anime.interceptor;

import com.computerapplicationtechnologycnus.sakura_anime.annotation.AuthRequired;
import com.computerapplicationtechnologycnus.sakura_anime.exception.AuthenticationException;
import com.computerapplicationtechnologycnus.sakura_anime.mapper.UserMapper;
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

    private final JwtUtil jwtUtil;
    private final UserMapper userMapper;

    public AuthInterceptor(JwtUtil jwtUtil,UserMapper userMapper){
        this.jwtUtil=jwtUtil;
        this.userMapper=userMapper;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        logger.info("AuthInterceptor triggered for URL: " + request.getRequestURI());

        // 检查 User-Agent
        String userAgent = request.getHeader("User-Agent");
        if (!isValidUserAgent(userAgent)) {
            logger.error("Invalid or suspicious User-Agent: " + userAgent);
            throw new AuthenticationException("Suspicious request detected!\n Please use a normal browser.");
        }

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
        //调试用，获取方法最低所需权限
        logger.info("Required minPermissionLevel: " + minPermissionLevel);
        // 获取请求中的 token
        String token = request.getHeader("Authorization");
        if (token == null || token.isEmpty()) {
            logger.error("Missing token in request.");
            throw new AuthenticationException("Missing token");
        }
        try {
            // 提取 token（去掉 Bearer 前缀）
            String jwtToken = token.startsWith("Bearer ") ? token.substring(7) : token;
//            String jwtToken = token;
            if (jwtUtil.isTokenExpired(jwtToken)) {
                logger.error("Token has expired.");
                throw new AuthenticationException("Token has expired");
            }
            // 验证Token有效性，并获取相关信息
            Claims claims = jwtUtil.getClaimsFromToken(jwtToken);
            logger.info(String.valueOf(claims)); //调试，查看Token讯息
            Integer userPermissionLevel = claims.get("permission", Integer.class);
            String username = claims.get("username", String.class); // 提取 username
            String passkeyFromToken = claims.get("passkey",String.class); //提取存储的密码
            String passkeyFromDatabase = userMapper.findUserPasskeyByUsername(username); //从数据库获取密码
            // 权限验证
            if(!passkeyFromToken.equals(passkeyFromDatabase)){
                throw new AuthenticationException("Premission Denied. Passkey not Match!");
            }
            //验证权限等级
            if (userPermissionLevel == null || userPermissionLevel > minPermissionLevel) {
                logger.error("User does not have sufficient permissions. Required: {}, Found: {}.",
                        minPermissionLevel, userPermissionLevel);
                throw new AuthenticationException("Insufficient permissions");
            }
            request.setAttribute("username", username); //把请求的username缓存
            logger.info("Token is valid and permissions are sufficient.");
            return true;

        } catch (JwtException e) {
            logger.error("Invalid token: " + e.getMessage());
            throw new AuthenticationException("Invalid token: " + e.getMessage());
        }
    }

    /**
     * UA检查器设定
     * @param userAgent
     * @return
     */
    private boolean isValidUserAgent(String userAgent) {
        if (userAgent == null || userAgent.isEmpty()) {
            logger.warn("No User-Agent provided");
            return false;
        }
        // 黑名单检查：常见的爬虫和恶意请求标识
        String[] suspiciousUserAgents = {
                "python-requests",
                "curl",
                "wget",
                "Apache-HttpClient",
                "Scrapy",
                "bot",
                "spider",
                "crawler"
        };
        for (String suspicious : suspiciousUserAgents) {
            if (userAgent.toLowerCase().contains(suspicious.toLowerCase())) {
                return false;
            }
        }
        // 可以添加更复杂的验证逻辑，比如正则表达式匹配
        // 例如：只允许常见浏览器的 User-Agent，这里还加了个Postman豁免
        if (!userAgent.matches("^(Mozilla|Chrome|Safari|Edge|Opera|Gecko|PostmanRuntime).*$")) {
            return false;
        }
        return true;
    }
}