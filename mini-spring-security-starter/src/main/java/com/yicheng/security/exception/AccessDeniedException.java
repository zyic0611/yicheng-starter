package com.yicheng.security.exception;


//没权限 403问题
public class AccessDeniedException extends RuntimeException {
    public AccessDeniedException(String message) {
        super(message);
    }
}
