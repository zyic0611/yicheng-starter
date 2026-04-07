package com.yicheng.lock.properties;


import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix="yicheng.lock")
public class DistributedLockProperties {
    private String address="redis://127.0.0.1:6379";
    private String password;
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }
}
