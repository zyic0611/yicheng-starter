package com.yicheng.limit.config;

import com.yicheng.limit.annotation.RateLimit;
import com.yicheng.limit.aspect.RateLimitAspect;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

@Configuration
public class RateLimitAutoConfiguration {
    /**
     * 核心亮点 1：将 Lua 脚本加载到 Spring 容器中
     * 为什么不在切面里每次读取文件？因为 IO 操作极慢！
     * 在系统启动时读取一次文件，缓存成 Bean，是高级开发的基本操作。
     */
    @Bean
    @ConditionalOnMissingBean
    public DefaultRedisScript<Long>  redisScript() {
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        // 从 classpath (即 resources 目录) 下加载 limit.lua 文件
        redisScript.setLocation(new ClassPathResource("limit.lua"));
        redisScript.setResultType(Long.class);//设置返回值类型位long
        return redisScript;
    }

    /**
     * 核心亮点 2：组装限流切面
     * 只要当前项目中存在 RedisTemplate 和刚才初始化的 limitScript，
     * Spring 就会自动把它们作为参数传进来，实例化你的 AOP 切面。
     */
    @Bean
    @ConditionalOnMissingBean
    public RateLimitAspect rateLimitAspect(RedisTemplate<String, Object> redisTemplate,
                                           DefaultRedisScript<Long> redisScript) {
        return new RateLimitAspect(redisTemplate, redisScript);
    }
}
