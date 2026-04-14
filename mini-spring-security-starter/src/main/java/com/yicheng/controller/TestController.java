package com.yicheng.controller;

import com.yicheng.security.Authentication.MiniAuthentication;
import com.yicheng.security.SecurityContextHolder;
import com.yicheng.security.user.UserDetail;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class TestController {
    @GetMapping("/api/userInfo")
    public String getUserInfo(){
        MiniAuthentication authentication =SecurityContextHolder.getAuthentication();//从上下文里获取Authentication
        if(authentication!=null&&authentication.isAuthenticated()){
            UserDetail user = (UserDetail) authentication.getPrincipal();//拿到当事人 类型为Object 需要强转
            return "当前操作人：" + user.getUsername() + "，角色：" + user.getRole();
        }
        return "未登录状态";
    }

    @GetMapping("/api/admin/deleteUser")
    public String deleteUser() {
        // 只有过了 AuthorizationFilter 的请求，才能打印出这句话
        return "超级管理员操作：高危删库动作执行成功！";
    }
}
