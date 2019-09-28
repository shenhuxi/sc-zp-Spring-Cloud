package com.sczp.order.controller;

import com.sczp.order.entity.Order;
import com.sczp.order.jpa.utils.ResultObject;
import com.sczp.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/order")
public class OderController {
    final OrderService orderService;

    @Autowired
    public OderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/testRedisKey")
    public String testRedisKey(String redisKey){
        return orderService.testRedisKey(redisKey);
    }

    @PostMapping("/createOrder")
    public ResultObject createOrder(@RequestBody Order order){
        if (orderService.createOrder(order)){
            return ResultObject.ok("订单创建成功！");
        }
        return ResultObject.error("服务器处理错误！");
    }
}
