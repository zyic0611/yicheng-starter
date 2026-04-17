package com.yicheng.security.service;

import com.yicheng.security.utils.JwtUtils; // 确保引入你的工具类
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;


public class TokenBlacklistService {


    private StringRedisTemplate redisTemplate;

    // 改为通过构造器传入 RedisTemplate，不再使用 @Autowired
    public TokenBlacklistService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 1. 退出登录：将 Token 加入黑名单
     * @param token 要废弃的 JWT
     * @param expirationTime 该 JWT 本身的过期时间戳 (毫秒)
     */
    public void blacklistToken(String token, long expirationTime) {
        long remainTime = expirationTime - System.currentTimeMillis();
        if (remainTime > 0) {
            // Key 为 Token，Value 随意，TTL 为剩余存活时间
            redisTemplate.opsForValue().set("security:blacklist:" + token, "revoked", remainTime, TimeUnit.MILLISECONDS);
        }
    }

    /**
     * 2. 检查 Token 是否在黑名单中
     */
    public boolean isBlacklisted(String token) {
        return Boolean.TRUE.equals(redisTemplate.hasKey("security:blacklist:" + token));
    }

    /**
     * 3. 获取用户当前的最新 Token (供 Provider 进行单点登录强校验)
     */
    public String getLatestToken(String username) {
        return redisTemplate.opsForValue().get("security:user_token:" + username);
    }

    /**
     * 4. 踢人逻辑：用户登录时调用，更新最新 Token，并将旧 Token 拉黑
     * @param username 用户名
     * @param newToken 刚签发的新 JWT
     * @param newTokenExpiration 新 JWT 的过期时间戳 (毫秒)
     */
    public void updateLatestToken(String username, String newToken, long newTokenExpiration) {
        // 先查出是否有旧的 Token
        String oldToken = getLatestToken(username);

        if (oldToken != null && !oldToken.equals(newToken)) {
            try {
                // 严谨操作：从旧 Token 中解析出它原本的过期时间，加入黑名单
                // 注意：这里需要你的 JwtUtils 提供解析过期时间的方法
                long oldExpiration = JwtUtils.parseToken(oldToken).getExpiration().getTime();
                blacklistToken(oldToken, oldExpiration);
            } catch (Exception e) {
                // 如果旧 Token 已经损坏或自然过期解析报错，直接忽略即可
            }
        }

        // 记录新 Token 映射，并且给这个映射也加上 TTL，防止垃圾数据堆积
        long remainTime = newTokenExpiration - System.currentTimeMillis();
        if (remainTime > 0) {
            redisTemplate.opsForValue().set("security:user_token:" + username, newToken, remainTime, TimeUnit.MILLISECONDS);
        }
    }
}