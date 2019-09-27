package com.sczp.order.feign_service;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import com.sczp.order.feign_service.callback.SystemApiCallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "system-service",fallback = SystemApiCallback.class)
public interface SystemApi {

    @HystrixCommand(commandProperties = {
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds",value = "1000")
    })
    @RequestMapping(value = "user/getUserByName",method = RequestMethod.GET)
    String getUserByName(@RequestParam(value = "name") String name);

    @RequestMapping(value = "user/testRedisKey",method = RequestMethod.GET)
    String testRedisKey(@RequestParam(value = "name") String redisKey);
}
