package com.sczp.order.controller;

import com.sczp.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order")
public class OderController {
    final OrderService orderService;

    @Autowired
    public OderController(OrderService orderService) {
        this.orderService = orderService;
    }
    @GetMapping("/createOrder")
    public String  createOrder(){
        String order = orderService.createOrder();
        return order;
    }
}
