package com.yicheng.security.filter;

import com.yicheng.security.Authentication.MiniAuthenticationProvider;
import com.yicheng.security.Authentication.MiniProviderManager;
import com.yicheng.security.Authentication.TokenAuthenticationProvider;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MiniFilterChainProxy implements Filter {

    // 内部维护的虚拟过滤器链
    private final List<Filter> virtualFilters = new ArrayList<>();

    public MiniFilterChainProxy(){
        List<MiniAuthenticationProvider>providers=new ArrayList<>();//检票员列表
        providers.add(new TokenAuthenticationProvider());//把token检票员加入列表
        MiniProviderManager Manager=new MiniProviderManager(providers);//创建一个保安队长
        //把保安队长和第一个检票员拍发给虚拟过滤链
        virtualFilters.add(new TokenAuthenticationFilter(Manager));//先验票 token 有没有这个用户
        virtualFilters.add(new AuthorizationFilter());//再查权限 该用户有没有这个权限

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
                         FilterChain chain)throws IOException, ServletException {
        System.out.println("🚨 [大管家] 成功拦截请求！当前手下保安数量: " + virtualFilters.size());

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
