package com.yicheng.security.filter;

import com.yicheng.security.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class SecurityContextHolderFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain chain) throws IOException, ServletException {

        // 1. 请求进来时：尝试从 Session 或其他地方恢复之前的身份
        // (对于前后端分离的 JWT 架构，这里通常为空，等后面的 TokenFilter 去填充)

        try {
            // 2. 放行，包裹住整条执行链！
            chain.doFilter(request, response);

        } finally {
            // 3. 核心：无论里面是成功还是抛了异常，最后一步统一下班，彻底清空 ThreadLocal！
            SecurityContextHolder.clearContext();
        }
    }
}
