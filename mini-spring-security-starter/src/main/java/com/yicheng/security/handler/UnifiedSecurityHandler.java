package com.yicheng.security.handler;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class UnifiedSecurityHandler implements
        MiniAuthenticationEntryPoint,MiniAccessDeniedHandler{
    @Override
    public void handle(HttpServletResponse rp, Exception accessDeniedException)throws IOException {
        renderJson(rp,403,"权限不足"+accessDeniedException.getMessage());
    }
    public void commence(HttpServletResponse rp, Exception authenticationException)throws IOException {
        renderJson(rp,401,"请登陆"+authenticationException.getMessage());
    }

    private void renderJson(HttpServletResponse response, int status, String msg)throws IOException {
        response.setStatus(status);
        response.setContentType("application/json;charset=utf-8");
        response.getWriter().write("{\"code\": " + status + ", \"message\": \"" + msg + "\"}");
    }
}
