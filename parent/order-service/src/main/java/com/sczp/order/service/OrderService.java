package com.sczp.order.service;

import com.sczp.order.feign_service.SystemApi;
import com.sczp.order.util.RedisService;
import com.sczp.order.util.threadPool.ThreadPoolInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;

@Service
public class OrderService {
    private final SystemApi systemApi;
    private final RedisService redisService;
    private final RedisTemplate redisTemplate;

    @Autowired
    public OrderService(SystemApi systemApi, RedisService redisService, RedisTemplate redisTemplate) {
        this.systemApi = systemApi;
        this.redisService = redisService;
        this.redisTemplate = redisTemplate;
    }

    public  String  createOrder(){
        String zhangSan = systemApi.getUserByName("张三");
        return "创建订单成功订单用户为："+zhangSan;
    }

    public String testRedisKey(String redisKey) {
        String systemResult = systemApi.testRedisKey("张三");
        ExecutorService threadPoolInstance = ThreadPoolInstance.getThreadPoolInstance();
        Runnable a = () -> {

        };

        threadPoolInstance.execute(a);
        //step1 处理逻辑 business.....
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return "msg:order处理成功,code:200 | "+systemResult;
    }
}
