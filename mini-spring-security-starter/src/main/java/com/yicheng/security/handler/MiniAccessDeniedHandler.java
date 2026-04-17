package com.yicheng.security.handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface MiniAccessDeniedHandler {
    void handle(HttpServletResponse rp,Exception accessDeniedException) throws IOException;
}
