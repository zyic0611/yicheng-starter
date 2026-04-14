package com.yicheng.security;

//上下文拥有者

import com.yicheng.security.Authentication.MiniAuthentication;

public class SecurityContextHolder {
    private static final ThreadLocal<MiniAuthentication> CONTEXT = new ThreadLocal<>();

    public static MiniAuthentication getAuthentication() {
        return CONTEXT.get();
    }

    public static void setAuthentication(MiniAuthentication  authentication) {
        CONTEXT.set(authentication);
    }

    public static void removeAuthentication() {//清理上下文
        CONTEXT.remove();
    }

}
