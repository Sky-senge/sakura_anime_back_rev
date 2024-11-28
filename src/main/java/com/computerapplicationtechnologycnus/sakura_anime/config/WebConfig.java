package com.computerapplicationtechnologycnus.sakura_anime.config;

import com.computerapplicationtechnologycnus.sakura_anime.interceptor.AuthInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private AuthInterceptor authInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/**");  // 拦截所有路径，进行权限校验
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 配置跨域规则
        registry.addMapping("/**") // 应用于所有路径
                .allowedOrigins("*") // 允许所有来源
                .allowedMethods("GET", "POST") // 允许的 HTTP 方法
                .allowedHeaders("*") // 允许所有请求头
                .allowCredentials(false) // 无需携带凭证（注意，Token还是会强制检查的，只是为了骗过跨域限制罢了）
                .maxAge(3600); // 预检请求的最大缓存时间
    }
}
