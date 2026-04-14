package com.yicheng.security.filter;

//从上下文拿出刚放进去的身份 看看他的 Role 是否满足当前访问 URL 的要求
// 不满足直接返回 403

import com.yicheng.security.Authentication.MiniAuthentication;
import com.yicheng.security.SecurityContextHolder;
import com.yicheng.security.user.UserDetail;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AuthorizationFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
                         FilterChain chain)throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        HttpServletRequest request = (HttpServletRequest) servletRequest;

        String uri=request.getRequestURI();

        if("/login".equals(uri)){
            chain.doFilter(request, response);
            return;
        }

        if(uri.startsWith("/api/admin")){
            //查身份 去authentication里拿用户
            MiniAuthentication auth = SecurityContextHolder.getAuthentication();

            // 没通行证？或者通行证没盖章？那是没登录！(401)
            // 虽然前面的 TokenFilter 会拦截无效 Token，但如果前端根本没发 Token，前面的 Filter 是直接放行的
            if (auth == null || !auth.isAuthenticated()) {
                sendError(response, HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized: 请先登录！");
                return;
            }

            // 提取当事人档案
            UserDetail user = (UserDetail) auth.getPrincipal();

            // 验明正身：如果角色不是 Admin，报权限不足！(403)
            // 回忆一下：之前在 TokenProvider 里，我们给 admin_token 设置的角色是 "Admin"
            if (!"Admin".equals(user.getRole())) {
                sendError(response, HttpServletResponse.SC_FORBIDDEN, "Forbidden: 越权访问！你没有管理员权限！");
                return;
            }
        }
        //如果不是管理员接口或者校验通过
        chain.doFilter(request, response);


    }

    // 提取一个发错误信息的私有方法，让代码清爽点
    private void sendError(HttpServletResponse rp, int status, String msg) throws IOException {
        rp.setStatus(status);
        rp.setContentType("application/json;charset=utf-8");
        rp.getWriter().write("{\"error\": \"" + msg + "\"}");
    }
}
