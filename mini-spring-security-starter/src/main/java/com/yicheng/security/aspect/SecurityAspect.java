package com.yicheng.security.aspect;

import com.yicheng.security.annotation.PreAuthorize;
import com.yicheng.security.exception.AccessDeniedException;
import com.yicheng.security.exception.AuthenticationException;
import com.yicheng.security.authentication.Authentication;
import com.yicheng.security.SecurityContextHolder;
import com.yicheng.security.user.UserDetail;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class SecurityAspect {
    @Around("@annotation(preAuthorize)")
    public Object checkPermission(ProceedingJoinPoint joinPoint, PreAuthorize preAuthorize) throws Throwable {
        String requiredRole = preAuthorize.value();

        Authentication authentication= SecurityContextHolder.getAuthentication();

        //未登录
        if(authentication==null||!authentication.isAuthenticated()){
            throw new AuthenticationException("认证失败：你还未登录，无法访问该资源！");
        }

        UserDetail user=(UserDetail) authentication.getPrincipal();

        //角色不匹配
        if (!requiredRole.equals(user.getRole())) {
            throw new AccessDeniedException("越权访问：该接口需要 [" + requiredRole + "] 角色，而你是 [" + user.getRole() + "]");
        }

        // 校验通过，放行！执行真正的 Controller 业务逻辑
        return joinPoint.proceed();
    }
}
