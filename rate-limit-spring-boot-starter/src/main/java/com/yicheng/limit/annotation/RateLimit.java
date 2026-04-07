package com.yicheng.limit.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimit {

    String key()default "default_limit_";

    int time() default 60;//时间窗口

    int count() default 10;//访问上线

    String message() default "系统繁忙 请稍后再试";
    //触发限流跑出的提示信息 能让不同接口在限流的时候 返回前端不同的报错语。
}
