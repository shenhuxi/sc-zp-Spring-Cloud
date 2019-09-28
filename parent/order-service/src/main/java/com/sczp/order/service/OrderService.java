package com.sczp.order.service;

import com.sczp.order.entity.Order;

public interface OrderService {
    String testRedisKey(String redisKey);

    boolean createOrder(Order order);
}
