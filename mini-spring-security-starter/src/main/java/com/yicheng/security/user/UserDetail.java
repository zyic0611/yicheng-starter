package com.yicheng.security.user;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserDetail {
    private String username;
    private String role; // 角色，比如 "Admin", "User"
    // 未来还可以加：密码、账号是否过期、账号是否被锁定等字段
}
