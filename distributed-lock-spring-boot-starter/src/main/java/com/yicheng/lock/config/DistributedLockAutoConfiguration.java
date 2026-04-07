package com.yicheng.lock.config;

import com.yicheng.lock.aspect.DistributedLockAspect;
import com.yicheng.lock.properties.DistributedLockProperties;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(RedissonClient.class)//工程里有redisson依赖的时候 才加载
@EnableConfigurationProperties(DistributedLockProperties.class)
public class DistributedLockAutoConfiguration {
    //告诉spring 如果用户没配redisson 该starter帮忙配一个 并且把切面类注册进去

    //注册redisson client 如果用户没配 就按照yml里的地址配一恶搞
    @Bean
    @ConditionalOnMissingBean
    public RedissonClient redissonClient(DistributedLockProperties properties) {
        Config config = new Config();
        config.useSingleServer()
                .setAddress(properties.getAddress())
                .setPassword(properties.getPassword());

        return Redisson.create(config);
    }

    //注册切面类 让Spring容器识别 启用AOP拦截逻辑
    @Bean
    @ConditionalOnMissingBean
    public DistributedLockAspect distributedLockAspect(RedissonClient redissonClient) {
        return new DistributedLockAspect(redissonClient);
    }
}
