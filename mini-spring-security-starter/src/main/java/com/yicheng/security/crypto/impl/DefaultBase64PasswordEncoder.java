package com.yicheng.security.crypto.impl;

import com.yicheng.security.crypto.PasswordEncoder;

import java.util.Base64;

public class DefaultBase64PasswordEncoder implements PasswordEncoder {

    @Override
    public String encode(String rawPassword) {
        return Base64.getEncoder().encodeToString(rawPassword.getBytes());
    }

    @Override
    public boolean matches(String rawPassword, String encodedPassword) {
        //前端传来的明文密码和数据库里的密码比对
        return encode(rawPassword).equals(encodedPassword);
    }
}
