package com.yicheng.security.authentication;

//安保队长：核心方法：传入未认证的票据，返回已认证的票据
public interface AuthenticationManager {
    Authentication authenticate(Authentication authentication);
}
