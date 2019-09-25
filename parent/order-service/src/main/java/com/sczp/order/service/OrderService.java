package com.sczp.order.service;

import com.sczp.order.feign_service.SystemApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderService {
    private final SystemApi systemApi;

    @Autowired
    public OrderService(SystemApi systemApi) {
        this.systemApi = systemApi;
    }

    public  String  createOrder(){
        String zhangsan = systemApi.getUserByName("张三");
        return "创建订单成功订单用户为："+zhangsan;
    }
}
