package com.yicheng.limit.aspect;

import com.yicheng.limit.annotation.RateLimit;
import com.yicheng.limit.excetion.LimitException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;

@Aspect
public class RateLimitAspect {
    private static final Logger log = LoggerFactory.getLogger(RateLimitAspect.class);

    private final RedisTemplate<String, Object> redisTemplate;

    private final DefaultRedisScript<Long> redisScript;//lua脚本对象

    public RateLimitAspect(RedisTemplate<String, Object> redisTemplate,DefaultRedisScript<Long> redisScript) {
        this.redisTemplate = redisTemplate;
        this.redisScript = redisScript;
    }

    @Around("@annotation(rateLimit)")
    public Object around(ProceedingJoinPoint joinPoint, RateLimit rateLimit) throws Throwable {

        // 获取 Spring 上下文中的 Request 对象
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();

        //获取ip
        String ip = request.getRemoteAddr();

        //动态拼接
        String final_key=rateLimit.key()+":"+ip;


        //拿到注解里的参数
        int time=rateLimit.time();
        int count=rateLimit.count();
        Long current=null;
        try{
            //执行lua脚本限流
            current=redisTemplate.execute(
                    redisScript,
                    Collections.singletonList(final_key),
                    count,
                    time
            );


        }catch (Exception e){
            //lua脚本执行失败 脚本错误或者redis挂了
            //通常为了业务正常进行 选择放行
            //这里就是兜底
            log.info("限流组件 Redis 交互异常，执行降级放行策略", e);
        }
        //逻辑判断不在trycatch外 防止限流被 catch吞掉
        if(current!=null&&current.intValue()>count){
            log.warn("接口限流触发！Key: {}, 当前尝试次数: {}, 限制次数: {}", final_key, current, count);
            // 抛出异常，阻止业务代码执行
            throw new LimitException(rateLimit.message());
        }
        log.info("接口访问通过，Key: {}, 当前计数: {}", final_key, current);
        //没到达限流阈值 放心业务方法
        return joinPoint.proceed();
    }
}
