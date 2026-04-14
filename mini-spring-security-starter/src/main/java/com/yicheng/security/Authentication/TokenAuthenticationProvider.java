package com.yicheng.security.Authentication;

import com.yicheng.security.user.UserDetail;

//专门检验token的provider
public class TokenAuthenticationProvider implements MiniAuthenticationProvider{
    @Override
    public MiniAuthentication authenticate(MiniAuthentication authentication) {
        // 1. 拿到未认证票据里的 Token
        String token = (String) authentication.getCredentials();

        // 2. 剥离 Bearer，进行真实校验 (这里依然先用模拟的，真实情况这里会调用 UserDetailsService 去查数据库)
        String username = token.substring(7);
        UserDetail user = null;
        if ("admin_token".equals(username)) {
            user = new UserDetail("AdminUser", "Admin");
        } else if ("user_token".equals(username)) {
            user = new UserDetail("UserUser", "User");
        }

        // 3. 如果查到了人，签发【已认证】的新票据！
        if (user != null) {
            return new MiniAuthentication(token,user); // 调用了两个参数的构造器，authenticated 变成了 true
        }

        return null; // 验证不通过
    }

    @Override
    public boolean supports(Class<?> authenticationType) {
        // 只要传过来的是我们定义的 MiniAuthentication 类型，我就接单
        return MiniAuthentication.class.isAssignableFrom(authenticationType);
    }
}
