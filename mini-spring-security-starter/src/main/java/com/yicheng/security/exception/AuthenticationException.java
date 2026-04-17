package com.yicheng.security.exception;


//没登录 401问题
public class AuthenticationException extends RuntimeException {
    public AuthenticationException(String message) {
        super(message);
    }
}
