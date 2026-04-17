package com.yicheng.security.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;

public class JwtUtils {
    // 1. 密钥：必须大于 256 位 (真实项目中通常放在 application.yml 里)
    // 这里我们直接生成一个固定的安全密钥用于测试
    private static final String SECRET_STRING = "YiChengMiniSecurityFrameworkSuperSecretKey123456";
    private static final Key SECRET_KEY = Keys.hmacShaKeyFor(SECRET_STRING.getBytes());

    // 2. 过期时间：设置为 2 小时 (单位：毫秒)
    private static final long EXPIRATION_TIME = 1000 * 60 * 60 * 2;

    /**
     * 签发 JWT (登录成功时调用)
     * @param username 用户名
     * @param role 角色
     * @return 完整的 JWT 字符串
     */
    public static String generateToken(String username, String role) {
        return Jwts.builder()
                .setSubject(username) // 主题通常放用户名
                .claim("role", role)  // 往 Payload 里塞自定义数据 (角色)
                .setIssuedAt(new Date()) // 签发时间
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) // 过期时间
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256) // 盖上防伪钢印！
                .compact();
    }

    /**
     * 解析并验证 JWT (每次请求经过过滤器时调用)
     * @param token 前端传来的 Token
     * @return 解析后的 Claims (包含 Payload 里的所有信息)
     */
    public static Claims parseToken(String token) {
        // 如果 Token 过期、被篡改，这里会直接抛出 JwtException 等异常！
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
