package com.yicheng.controller;

import com.yicheng.security.annotation.PreAuthorize;
import com.yicheng.security.authentication.Authentication;
import com.yicheng.security.SecurityContextHolder;
import com.yicheng.security.user.UserDetail;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    @PreAuthorize("admin")
    @GetMapping("/api/userInfo")
    public String getUserInfo(){
        Authentication authentication =SecurityContextHolder.getAuthentication();//从上下文里获取Authentication
        if(authentication!=null&&authentication.isAuthenticated()){
            UserDetail user = (UserDetail) authentication.getPrincipal();//拿到当事人 类型为Object 需要强转
            return "当前操作人：" + user.getUsername() + "，角色：" + user.getRole();
        }
        return "未登录状态";
    }

}
