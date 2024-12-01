package com.computerapplicationtechnologycnus.sakura_anime.handler;

import com.computerapplicationtechnologycnus.sakura_anime.common.ResultMessage;
import com.computerapplicationtechnologycnus.sakura_anime.exception.AuthenticationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    //旧的方法
//    @ExceptionHandler(AuthenticationException.class)
//    public ResponseEntity<String> handleAuthenticationException(AuthenticationException e) {
//        return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
//    }

    //转为使用ResultMessage返回讯息
    @ExceptionHandler(AuthenticationException.class)
    public ResultMessage handleAuthenticationException(AuthenticationException e) {
        return ResultMessage.message(false,"",e.getMessage());
    }
}
