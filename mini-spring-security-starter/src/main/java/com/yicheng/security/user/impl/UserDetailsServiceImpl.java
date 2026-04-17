package com.yicheng.security.user.impl;

import com.yicheng.security.crypto.PasswordEncoder;
import com.yicheng.security.user.UserDetail;
import com.yicheng.security.user.UserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private PasswordEncoder passwordEncoder;


    @Override
    public UserDetail loadUserByUsername(String username) {
        // 先手动写死一个，模拟从数据库查出来的样子
        if ("admin".equals(username)) {
            // 假设密码是 123456（对应的 Base64 密文是 MTIzNDU2）
            return new UserDetail("admin", "admin", passwordEncoder.encode("123456"));
        }else if ("user".equals(username)) {
            return new UserDetail("user", "user", passwordEncoder.encode("123456"));
        }
        return null;
    }
}
