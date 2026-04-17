package com.yicheng.security.authentication;

//校验员接口
public interface AuthenticationProvider {
    //执行真正的校验逻辑

    // 核心方法：执行真正的校验逻辑
    Authentication authenticate(Authentication authentication);

    // 策略匹配：判断当前这个 Provider 能不能处理这种类型的票据
    boolean supports(Class<?> authenticationType);
    //所以不同的票据要构建不同的检票员
}

