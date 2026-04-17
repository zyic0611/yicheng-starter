package com.yicheng.security.filter;

import com.yicheng.security.authentication.AuthenticationProvider;
import com.yicheng.security.authentication.ProviderManager;
import com.yicheng.security.authentication.TokenAuthenticationProvider;
import com.yicheng.security.handler.UnifiedSecurityHandler;
import com.yicheng.security.service.TokenBlacklistService;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class FilterChainProxy implements Filter {

    private final TokenBlacklistService blacklistService;
    // 内部维护的虚拟过滤器链
    private final List<Filter> virtualFilters = new ArrayList<>();

    // 【核心改动】：只保留这一个构造函数，并把原来的初始化逻辑搬进来
    public FilterChainProxy(TokenBlacklistService blacklistService) {
        this.blacklistService = blacklistService;

        // 1. 初始化异常处理器
        UnifiedSecurityHandler handler = new UnifiedSecurityHandler();

        // 2. 组装检票员列表
        List<AuthenticationProvider> providers = new ArrayList<>();

        // 【关键点】：创建 Provider 的时候，把接力棒传给它！
        providers.add(new TokenAuthenticationProvider(this.blacklistService));

        // 3. 创建保安队长
        ProviderManager manager = new ProviderManager(providers);

        // 4. 按顺序填充虚拟过滤链
        virtualFilters.add(new SecurityContextHolderFilter());    // 第一层：清理者
        virtualFilters.add(new ExceptionTranslationFilter(handler, handler)); // 第二层：异常捕获
        virtualFilters.add(new TokenAuthenticationFilter(manager)); // 第三层：Token校验（内含保安队长）
        virtualFilters.add(new AuthorizationFilter());            // 第四层：基础鉴权
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
                         FilterChain chain)throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        // 白名单判断可以放在安保总管这里（真实源码中会使用 RequestMatcher 进行更复杂的匹配）
        if ("/login".equals(request.getRequestURI())) {
            chain.doFilter(request, response);
            return;
        }

        // 核心：启动虚拟调用链！
        VirtualFilterChain virtualChain = new VirtualFilterChain(virtualFilters, chain);
        virtualChain.doFilter(request, response);

    }

    /**
     * 内部类：模拟 Spring Security 的虚拟过滤链流转机制
     */
    private static class VirtualFilterChain implements FilterChain{
        private final List<Filter> additionalFilters    ;
        private final FilterChain originalChain;
        private int currentPosition = 0;
        public VirtualFilterChain(List<Filter> filters, FilterChain originalChain) {
            this.additionalFilters = filters;
            this.originalChain = originalChain;// 记住原本 Tomcat 的链条，等我们自己的跑完，还得还给 Tomcat

        }

        @Override
        public void doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException {
            // 如果虚拟过滤器还没执行完，就按顺序取出一个执行
            if (this.currentPosition < this.additionalFilters.size()) {
                Filter nextFilter = this.additionalFilters.get(this.currentPosition++);
                // 注意这里：把当前的 VirtualFilterChain 传给了下一个 Filter
                // 这样下一个 Filter 里调用 chain.doFilter 时，又会回到这里执行下一轮！
                nextFilter.doFilter(request, response, this);
            } else {
                // 我们所有的虚拟过滤器都执行完了！
                // 把请求还给 Tomcat 的原生 FilterChain，最终会走到你的 Controller
                this.originalChain.doFilter(request, response);
            }
        }
    }
}
