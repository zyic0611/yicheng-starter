package com.yicheng.security.filter;

import com.yicheng.security.exception.AuthenticationException;
import com.yicheng.security.authentication.Authentication;
import com.yicheng.security.SecurityContextHolder;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class AuthorizationFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        HttpServletRequest rq = (HttpServletRequest) request;
        String uri = rq.getRequestURI();

        // 1. 白名单放行区：登录接口、公开接口，直接放行
        if ("/login".equals(uri) || uri.startsWith("/api/public")) {
            chain.doFilter(request, response);
            return;
        }

        // 2. 核心兜底逻辑：除了白名单，任何请求访问我们系统的其他接口，【必须是已登录状态】！
        Authentication auth = SecurityContextHolder.getAuthentication();

        // 如果保险箱里没东西，或者没盖章，说明没登录 / Token 是假的
        if (auth == null || !auth.isAuthenticated()) {
            // 💡 极致优雅：不需要再写 response.getWriter() 了！
            // 直接向外抛出异常！站在门口的“公关经理 (ExceptionTranslationFilter)”会完美接住它并返回 401 JSON！
            throw new AuthenticationException("兜底拦截：你还没有登录，无法访问系统资源！");
        }

        // 3. 兜底校验通过（说明是个合法用户）。放行！交给里面的 AOP 切面去判断他的角色配不配。
        chain.doFilter(request, response);
    }
}
