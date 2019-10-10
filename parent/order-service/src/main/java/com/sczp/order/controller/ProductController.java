package com.sczp.order.controller;

import com.sczp.order.service.ProductService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
@RequestMapping("/product")
@Api("产品管理")
public class ProductController {

    final ProductService productService;
    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }


    @GetMapping("/seckillTest")
    @ApiOperation(value = "产品管理管理+", notes = "秒杀产品")
    public String seckillProductTest(String SerialNumber, Integer number,Long userId) {
        if (productService.seckillProduct(SerialNumber,number,userId)){
            return "秒杀成功,请查看邮箱！";
        }
        return "库存不足";
    }

}
