package com.yicheng.security.authentication;

import java.util.List;

public class ProviderManager implements AuthenticationManager {

    private final List<AuthenticationProvider> providers;//检票员列表

    public ProviderManager(List<AuthenticationProvider> providers) {
        this.providers = providers;
    }

    @Override
    public Authentication authenticate(Authentication authentication) {
        for(AuthenticationProvider provider : providers) {
            if (provider.supports(authentication.getClass())) {
                //如果由一个检票员能处理
                Authentication result = provider.authenticate(authentication);
                if (result != null && result.isAuthenticated()) {
                    return result; // 认证成功，直接返回盖了章的票据
                }
            }
        }
        // 如果所有检验员都搞不定，说明认证失败
        throw new RuntimeException("认证失败：没有找到合适的 Provider 或凭证错误");
    }
}
