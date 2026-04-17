package com.yicheng.security.user;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserDetail {
    private String username;
    private String role;
    private String password;

    public UserDetail(String username, String role) {
        this.username = username;
        this.role = role;
    }
}
