package com.yicheng.security.Authentication;

//安保队长：核心方法：传入未认证的票据，返回已认证的票据
public interface MiniAuthenticationManager {
    MiniAuthentication authenticate(MiniAuthentication miniAuthentication);
}
