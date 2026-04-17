package com.yicheng.security.handler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

//校验没通过 处理身份 401问题
public interface MiniAuthenticationEntryPoint {
    void commence(HttpServletResponse rp, Exception authException) throws IOException;
}
