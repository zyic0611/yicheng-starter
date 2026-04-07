package com.yicheng.demo.controller;

import com.yicheng.limit.annotation.RateLimit;
import com.yicheng.lock.annotation.DistributedLock;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class TestController {

    // 测试限流：针对这个接口，10秒内最多只允许访问 3 次
    @GetMapping("/test/limit")
    @RateLimit(key = "test_api", time = 10, count = 3, message = "你点得太快啦，服务器要冒烟了！")
    public Map<String,Object> testLimit() {
        Map<String,Object> rst = new HashMap<>();//组装标准的json格式 预备返回前端
        rst.put("code", 200);
        rst.put("msg", "请求成功！正在处理业务逻辑...");
        rst.put("data",null);
        return rst;
    }

    @GetMapping("/test/buy")
    @DistributedLock(key="iphone15",waitTime = 0,leaseTime = 10)
    public Map<String,Object> testBuy() throws InterruptedException {
        Map<String,Object> rst = new HashMap<>();//组装标准的json格式 预备返回前端
        System.out.println("==== 线程 " + Thread.currentThread().getName() + " 拿到锁，开始处理业务 ====");
        // 核心：让当前线程睡 5 秒，模拟复杂的数据库扣减库存操作
        Thread.sleep(5000);
        System.out.println("==== 线程 " + Thread.currentThread().getName() + " 业务处理完成 ====");
        rst.put("code", 200);
        rst.put("msg", "恭喜你，抢购成功");
        rst.put("data",null);
        return rst;
    }
}