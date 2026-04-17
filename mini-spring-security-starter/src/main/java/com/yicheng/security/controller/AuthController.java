package com.yicheng.security.controller;


import com.yicheng.security.crypto.PasswordEncoder;
import com.yicheng.security.dto.LoginReq;
import com.yicheng.security.service.TokenBlacklistService;
import com.yicheng.security.user.UserDetail;
import com.yicheng.security.user.UserDetailsService;
import com.yicheng.security.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

//作用是核对密码 签发token
@RestController
public class AuthController {

    //用户信息查询
    @Autowired
    private UserDetailsService userDetailsService;

    //编码器
    @Autowired
    private PasswordEncoder passwordEncoder;

    //redis 黑名单
    @Autowired(required = false)
    private TokenBlacklistService blacklistService;



    @PostMapping("/login")
    public Map<String,Object> login(@RequestBody LoginReq req){

        //准备返回给前端的json数据
        Map<String,Object> result = new HashMap<>();

        //提取账号密码
        String username=req.getUsername();
        String password=req.getPassword();//前端传来的明文

        //调用查询用户信息接口 用名字去数据库里查出所有信息
        UserDetail user=userDetailsService.loadUserByUsername(username);

        if(user==null){
            result.put("code", 401);
            result.put("message", "登录失败：用户不存在！");
            return result;
        }
        //校验密码（用加密器比对：明文 vs 数据库里的密文）
        if(!passwordEncoder.matches(password,user.getPassword())){
            result.put("code", 401);
            result.put("message", "密码错误");
            return result;
        }

        //校验通过！签发 JWT
        // 这里的角色信息 user.getRole() 是从数据库里实时查出来的
        String token = JwtUtils.generateToken(user.getUsername(), user.getRole());

        //实现 单设备登录
        if (blacklistService != null) {
            try {
                // 解析出新 Token 的准确过期时间戳 (毫秒)
                // 注意：这里调用了 JwtUtils 的 parseToken，也可以在 JwtUtils 里专门写一个 getExpiration(token) 方法
                long expirationTime = JwtUtils.parseToken(token).getExpiration().getTime();

                // 登记新 Token，内部会自动把旧 Token 拉黑！
                blacklistService.updateLatestToken(username, token, expirationTime);
            } catch (Exception e) {
                // 打印日志，但不阻断用户正常登录
                System.err.println("更新 Redis Token 映射失败：" + e.getMessage());
            }
        }
        result.put("code", 200);
        result.put("message", "登录成功！");
        result.put("token", token);

        return result;
    }

    @PostMapping("/logout")
    public Map<String, Object> logout(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        Map<String, Object> result = new HashMap<>();

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7).trim();

            if (blacklistService != null) {
                try {
                    // 获取当前 Token 的剩余存活时间，并加入黑名单
                    long expirationTime = JwtUtils.parseToken(token).getExpiration().getTime();
                    blacklistService.blacklistToken(token, expirationTime);
                } catch (Exception e) {
                    // Token 可能已经过期或损坏，无需处理
                }
            }
        }

        // 退出登录通常都返回成功，即使用户传了个假 Token
        result.put("code", 200);
        result.put("message", "退出登录成功");
        return result;
    }
}
