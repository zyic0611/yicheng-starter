package com.yicheng.security;

//上下文拥有者

import com.yicheng.security.authentication.Authentication;

public class SecurityContextHolder {
    private static final ThreadLocal<Authentication> CONTEXT = new ThreadLocal<>();

    public static Authentication getAuthentication() {
        return CONTEXT.get();
    }

    public static void setAuthentication(Authentication authentication) {
        CONTEXT.set(authentication);
    }

    public static void clearContext() {//清理上下文
        CONTEXT.remove();
    }

}
