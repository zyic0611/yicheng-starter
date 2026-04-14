package com.yicheng.security.filter;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

//自定义配置规则
@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<MiniFilterChainProxy> filterRegistrationBean() {
        System.out.println("🚀🚀🚀 [系统启动] FilterConfig 配置类被 Spring 加载了！");
        //注册一个过滤器
        FilterRegistrationBean<MiniFilterChainProxy> registration = new FilterRegistrationBean<>();

        //把自定义过滤器放入
        registration.setFilter(new MiniFilterChainProxy());//注入自定义过滤器

        //配置自定义过滤器
        registration.addUrlPatterns("/*");//配置 只拦截所有请求

        // 3. 设置过滤器名字和执行顺序 (数字越小越先执行)
        registration.setName("miniFilterChainProxy");
        registration.setOrder(1);

        return registration;

    }
}
