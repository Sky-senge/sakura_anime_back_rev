package com.computerapplicationtechnologycnus.sakura_anime.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义注解，用于标记需要身份验证和权限验证的方法。
 */
@Target(ElementType.METHOD)  // 仅用于方法
@Retention(RetentionPolicy.RUNTIME)  // 在运行时保留注解
//@Documented//说明该注解将被包含在javadoc中
public @interface AuthRequired {

    // 允许访问的最低权限等级，默认为1（普通用户）
    int minPermissionLevel() default 1;
}
