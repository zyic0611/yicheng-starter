package com.yicheng.security.filter;

import com.yicheng.security.service.TokenBlacklistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//自定义配置规则
@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<FilterChainProxy> filterRegistrationBean(
            // 关键：告诉 Spring，我需要这个零件。如果没有配 Redis，这里会传 null 进来。
            @Autowired(required = false) TokenBlacklistService blacklistService
    ) {
        //注册一个过滤器
        FilterRegistrationBean<FilterChainProxy> registration = new FilterRegistrationBean<>();

        //把自定义过滤器放入
        registration.setFilter(new FilterChainProxy(blacklistService));//注入自定义过滤器

        //配置自定义过滤器
        registration.addUrlPatterns("/*");//配置 只拦截所有请求

        // 3. 设置过滤器名字和执行顺序 (数字越小越先执行)
        registration.setName("FilterChainProxy");
        registration.setOrder(1);

        return registration;

    }
}
