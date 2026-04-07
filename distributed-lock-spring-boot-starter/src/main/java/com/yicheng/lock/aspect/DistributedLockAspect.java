package com.yicheng.lock.aspect;
import com.yicheng.lock.annotation.DistributedLock;
import com.yicheng.lock.exception.LockException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

@Aspect
public class DistributedLockAspect {
    private static final Logger log = LoggerFactory.getLogger(DistributedLockAspect.class);

    // 核心依赖：操作 Redisson 的客户端
    private final RedissonClient redissonClient;

    public DistributedLockAspect(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    @Around("@annotation(distributedLock)")
    public Object around(ProceedingJoinPoint joinPoint, DistributedLock distributedLock)throws Throwable{
        // 1. 获取注解里的 key，拼装成 Redis 里的真正 key
        String lockKey = "lock:" + distributedLock.key();
        RLock lock = redissonClient.getLock(lockKey);//创建锁
        boolean acquired = false;

        try {
            log.info("线程 [{}] 尝试获取分布式锁: {}", Thread.currentThread().getName(), lockKey);

            // 2. 尝试加锁
            // waitTime: 等待获取锁的时间，leaseTime: 锁的持有时间
            acquired = lock.tryLock(distributedLock.waitTime(), distributedLock.leaseTime(), TimeUnit.SECONDS);

            if (acquired) {
                log.info("线程 [{}] 成功获取锁: {}", Thread.currentThread().getName(), lockKey);
                // 3. 拿到锁了，放行！开始执行真正的业务方法（比如抢单入库）
                return joinPoint.proceed();
            } else {
                // 没拿到锁，说明并发太高被拦住了
                log.warn("线程 [{}] 获取锁失败，业务被拦截: {}", Thread.currentThread().getName(), lockKey);
                // 真实项目里这里一般会抛出自定义的业务异常，比如 BusinessException("服务器繁忙")
                throw new LockException("当前排队人数较多，请稍后再试！");
            }
        } finally {
            // 4. 释放锁（极其关键的逻辑）
            // 必须判断 acquired == true（自己确实拿到锁了） 
            // 且 isHeldByCurrentThread() == true（当前锁确实还是自己线程持有的）
            if (acquired && lock.isHeldByCurrentThread()) {
                lock.unlock();
                log.info("线程 [{}] 释放分布式锁: {}", Thread.currentThread().getName(), lockKey);
            }
        }
    }

}
