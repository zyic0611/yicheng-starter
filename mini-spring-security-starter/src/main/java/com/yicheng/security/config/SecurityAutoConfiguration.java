package com.yicheng.security.config;

import com.yicheng.security.crypto.PasswordEncoder;
import com.yicheng.security.crypto.impl.DefaultBase64PasswordEncoder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SecurityAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public PasswordEncoder defaultPasswordEncoder() {
        System.out.println("🛡️ [Security] 业务系统未提供密码加密器，框架已启动默认的 Base64 密码加密器！");
        return new DefaultBase64PasswordEncoder();
    }


}
