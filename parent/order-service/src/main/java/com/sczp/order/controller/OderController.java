package com.sczp.order.controller;

import com.sczp.order.entity.Order;
import com.sczp.order.jpa.utils.ResultObject;
import com.sczp.order.service.OrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/order")
@Api("订单管理")
public class OderController {
    final OrderService orderService;

    @Autowired
    public OderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/testRedisKey")
    @ApiOperation(value = "分布式事务+", notes = "测试key")
    public String testRedisKey(@PathVariable String redisKey){
        return orderService.testRedisKey(redisKey);
    }

    @PostMapping("/createOrder")
    @ApiOperation(value = "订单管理+", notes = "创建订单")
    public ResultObject createOrder(@ApiParam(name = "查询条件", value = "json格式", required = true)
                                        @RequestBody Order order){
        if (orderService.createOrder(order)){
            return ResultObject.ok("订单创建成功！");
        }
        return ResultObject.error("服务器处理错误！");
    }
}
