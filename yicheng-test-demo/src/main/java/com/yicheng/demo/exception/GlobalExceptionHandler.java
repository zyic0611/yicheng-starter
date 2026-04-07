package com.yicheng.demo.exception;

import com.yicheng.limit.excetion.LimitException;
import com.yicheng.lock.exception.LockException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = LockException.class)
    public Map<String,Object> handleLockException(Exception e) {
        Map<String,Object> rst = new HashMap<>();//组装标准的json格式 预备返回前端
        rst.put("code", 500);
        rst.put("msg", e.getMessage());
        rst.put("data",null);
        return rst;
    }

    @ExceptionHandler(value = LimitException.class)
    public Map<String,Object> handleLimitException(Exception e) {
        Map<String,Object> rst = new HashMap<>();//组装标准的json格式 预备返回前端
        rst.put("code", 429);//429 标准的请求过多状态码
        rst.put("msg", e.getMessage());
        rst.put("data",null);
        return rst;
    }

    //捕获其他所以异常
    @ExceptionHandler(Exception.class)
    public Map<String, Object> handleGlobalException(Exception e) {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 500);
        result.put("msg", "系统开小差啦，请联系管理员！");
        // 真实项目里，这里还要加上 log.error("系统未知异常", e); 方便排查问题
        result.put("data", null);
        return result;
    }
}
