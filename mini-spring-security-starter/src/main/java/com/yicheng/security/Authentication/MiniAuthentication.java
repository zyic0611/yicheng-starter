package com.yicheng.security.Authentication;

import lombok.Data;

@Data
public class MiniAuthentication {
    private Object credentials;//凭证 比如密码 token
    private Object principal; //当事人 认证成功前可能是一个username 认证成功后是UserDetail对象
    private Boolean authenticated=false;//盖章 是否校验完成

    //构造一个未完成的票据 通常由filter调用
    public MiniAuthentication(Object credentials) {
        this.credentials = credentials;
        this.authenticated=false;
    }

    //构造完成的 由provider调用
    public MiniAuthentication(Object credentials, Object principal) {
        this.credentials = credentials;
        this.principal = principal;
        this.authenticated=true;
    }

    public Boolean isAuthenticated(){
        return authenticated;
    }





}
