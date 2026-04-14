package com.yicheng.security.filter;

import com.yicheng.security.Authentication.MiniAuthentication;
import com.yicheng.security.Authentication.MiniAuthenticationManager;
import com.yicheng.security.SecurityContextHolder;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

//只做token校验
public class TokenAuthenticationFilter implements Filter {

    // Filter 内部持有一个大队长
    private final MiniAuthenticationManager authenticationManager;

    public TokenAuthenticationFilter(MiniAuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)throws IOException, ServletException{

        System.out.println("👮 [保安] 开始验票！");

        HttpServletRequest rq = (HttpServletRequest) request;
        HttpServletResponse rp = (HttpServletResponse) response;


        //取出token
        String token = rq.getHeader("Authorization");

        //校验身份并且封装userdetail 实际上可以查库
        if(token != null&&token.startsWith("Bearer ")){
            try{
                //拿到字符串 组装原始票据
                MiniAuthentication miniAuthentication=new MiniAuthentication(token);

                //直接给大队长校验
                MiniAuthentication authenticationToken=authenticationManager.authenticate(miniAuthentication);

                //校验成功 存入上下文
                SecurityContextHolder.setAuthentication(authenticationToken);
            }catch (Exception e){
                // 认证失败，清理上下文并拦截
                SecurityContextHolder.removeAuthentication();
                rp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                rp.setContentType("application/json;charset=utf-8");
                rp.getWriter().write("{\"error\": \"" + e.getMessage() + "\"}");
                return; // 直接返回，不再向下执行
            }

        }

        // 无论验证是否成功，这个 Filter 的工作都做完了，放行给链条上的下一个过滤器
        // 注意：真正的拦截（抛 401）通常由链条最后面的 ExceptionTranslationFilter 来做，我们这里为了简化，依然直接放行
        try{
            //放行操作必须放在 try-finally 中，确保请求结束一定清理 ThreadLocal
            chain.doFilter(request, response);

        }finally {
            SecurityContextHolder.removeAuthentication();
        }
    }

}
