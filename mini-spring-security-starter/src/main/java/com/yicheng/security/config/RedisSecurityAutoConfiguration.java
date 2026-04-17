package com.yicheng.security.config;


import com.yicheng.security.service.TokenBlacklistService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

@Configuration
@ConditionalOnClass(StringRedisTemplate.class) // 先决条件：业务方的 pom 里必须有 redis 依赖
public class RedisSecurityAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean // 容错：如果业务方自己也写了一个同名的 Bean，就用业务方的，咱们框架不瞎掺和
    @ConditionalOnBean(RedisConnectionFactory.class) // 只有业务方的 yml 里配了 redis 地址，连上了，咱们才创建
    public TokenBlacklistService tokenBlacklistService(StringRedisTemplate redisTemplate) {
        // 只有条件全部满足，才会 new 这个黑名单服务
        System.out.println("业务系统开启了redis服务 系统开启黑名单 防止用户重新登录功能。");
        return new TokenBlacklistService(redisTemplate);
    }
}