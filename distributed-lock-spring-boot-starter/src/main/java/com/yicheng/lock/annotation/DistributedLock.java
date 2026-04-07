package com.yicheng.lock.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)//运行的时候 通过反射 去读取 类有没有DistributedLock注解
public @interface DistributedLock {
    String key(); //锁名称

    long waitTime() default 0;//最大等待时间

    long leaseTime() default -1;//看门狗机制
}
