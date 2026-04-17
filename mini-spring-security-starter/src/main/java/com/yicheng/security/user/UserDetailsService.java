package com.yicheng.security.user;

public interface UserDetailsService {
    UserDetail loadUserByUsername(String username);
}
