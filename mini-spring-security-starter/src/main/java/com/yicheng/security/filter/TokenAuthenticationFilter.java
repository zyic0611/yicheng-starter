package com.yicheng.security.filter;

import com.yicheng.security.exception.AuthenticationException;
import com.yicheng.security.authentication.Authentication;
import com.yicheng.security.authentication.AuthenticationManager;
import com.yicheng.security.SecurityContextHolder;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

//只做token校验
public class TokenAuthenticationFilter implements Filter {

    // Filter 内部持有一个大队长
    private final AuthenticationManager authenticationManager;

    public TokenAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)throws IOException, ServletException{


        HttpServletRequest rq = (HttpServletRequest) request;
        HttpServletResponse rp = (HttpServletResponse) response;


        //取出token
        String token = rq.getHeader("Authorization");

        //校验身份并且封装userdetail 实际上可以查库
        if(token != null&&token.startsWith("Bearer ")){
            try {

                // 这样得到的 realToken 才是纯净的 JWT 字符串 "eyJhbG..."
                String realToken = token.substring(7).trim();

                // 3. 将干净的 Token 包装进票据
                Authentication authentication = new Authentication(realToken);

                // 4. 让大队长去校验
                Authentication authenticationToken = authenticationManager.authenticate(authentication);

                // 5. 校验成功 存入上下文
                SecurityContextHolder.setAuthentication(authenticationToken);
            }catch (Exception e){
                throw new AuthenticationException("认证失败：" + e.getMessage());
            }

        }

        chain.doFilter(request, response);
    }

}
