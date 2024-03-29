package com.sczp.order.feign_service.callback;

import com.sczp.order.feign_service.SystemApi;
import org.springframework.stereotype.Component;

@Component
public class SystemApiCallback implements SystemApi {
    @Override
    public String getUserByName(String name) {
        return "sorry 获取用户信息失败！";
    }

    @Override
    public String testRedisKey(String redisKey) {
        return "sorry 用户模块处理超时！";
    }
}
