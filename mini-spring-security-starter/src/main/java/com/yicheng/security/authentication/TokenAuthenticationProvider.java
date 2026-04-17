package com.yicheng.security.authentication;

import com.yicheng.security.exception.AuthenticationException;
import com.yicheng.security.service.TokenBlacklistService;
import com.yicheng.security.user.UserDetail;
import com.yicheng.security.utils.JwtUtils;
import io.jsonwebtoken.Claims;

//专门检验token的provider
public class TokenAuthenticationProvider implements AuthenticationProvider {

    // 1. 去掉 @Autowired，加上 final
    private final TokenBlacklistService blacklistService;

    // 2. 提供一个构造方法（允许传 null，即不使用 Redis 的降级情况）
    public TokenAuthenticationProvider(TokenBlacklistService blacklistService) {
        this.blacklistService = blacklistService;
    }



    @Override
    public Authentication authenticate(Authentication authentication) {
        // 1. 拿到未认证票据里的 Token
        String token = (String) authentication.getCredentials();

        try{
            // 2.物理校验 交给工具类验签 过期时间
            // 如果过期了、密钥不对、被篡改了，这里会直接抛出异常！
            Claims claims = JwtUtils.parseToken(token);

            //状态校验 redis黑名单拦截
            if(blacklistService!=null&& blacklistService.isBlacklisted(token)){
                throw  new AuthenticationException("您的登陆状态已经失效，可能在别处登录或已退出。");
            }
            // 3. 验签通过！从信封(Payload)里把信息拿出来
            String username = claims.getSubject();
            String role = claims.get("role", String.class);

            //踢人逻辑 判断token发过没
            if(blacklistService!=null){
                String latestToken=blacklistService.getLatestToken(username);//查出最新token
                if(latestToken!=null&&!latestToken.equals(token)){
                    //如果确实有最新token 并且当前token并非最新的token 当前token需要被踢
                    throw new AuthenticationException("账号在其他设备登录，您已被迫下线");

                }
            }
            // 4. 重建当事人信息
            UserDetail user = new UserDetail(username, role);
            // 5. 签发【已认证】的最终票据
            return new Authentication(token, user);

        }catch (Exception e){
            throw new AuthenticationException("Token 无效或已过期！详情：" + e.getMessage());
        }


    }

    @Override
    public boolean supports(Class<?> authenticationType) {
        // 只要传过来的是我们定义的 MiniAuthentication 类型，我就接单
        return Authentication.class.isAssignableFrom(authenticationType);
    }
}
