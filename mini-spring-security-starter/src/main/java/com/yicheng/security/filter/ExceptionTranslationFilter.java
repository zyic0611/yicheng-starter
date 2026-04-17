package com.yicheng.security.filter;

import com.yicheng.security.exception.AccessDeniedException;
import com.yicheng.security.exception.AuthenticationException;
import com.yicheng.security.handler.MiniAccessDeniedHandler;
import com.yicheng.security.handler.MiniAuthenticationEntryPoint;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

//异常捕获过滤器
public class ExceptionTranslationFilter implements Filter {
    private final MiniAccessDeniedHandler accessDeniedHandler;//创建拦截工具 拦截403 权限不足
    private final MiniAuthenticationEntryPoint authenticationEntryPoint; //拦截401 未登录

    public ExceptionTranslationFilter(MiniAccessDeniedHandler accessDeniedHandler,MiniAuthenticationEntryPoint authenticationEntryPoint) {
        this.accessDeniedHandler = accessDeniedHandler;
        this.authenticationEntryPoint = authenticationEntryPoint;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain)throws IOException, ServletException {
        try{
            chain.doFilter(request,response);//直接放行 不做过滤
        }catch (Exception e){
            // Tomcat 包装 Servlet 异常时，真实的异常经常被包裹在最里面，我们需要把它剥出来
            Throwable cause = e.getCause() != null ? e.getCause() : e;
            HttpServletResponse rp = (HttpServletResponse) response;
            // 如果是认证异常（401相关，比如我们自己定义的异常）
            if (cause instanceof AuthenticationException) {
                authenticationEntryPoint.commence(rp, (Exception) cause);
            }
            // 💡 精确打击 2：是授权异常吗？
            else if (cause instanceof AccessDeniedException) {
                accessDeniedHandler.handle(rp, (Exception) cause);
            }
            // 其他诸如 NullPointerException、空指针、SQL 报错，直接往上抛，不归安全框架管！
            else {
                throw e;
            }
        }
    }
}
