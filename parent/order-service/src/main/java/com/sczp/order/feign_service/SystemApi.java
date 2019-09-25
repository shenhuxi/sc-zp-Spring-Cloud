package com.sczp.order.feign_service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "system-service")
public interface SystemApi {

    @RequestMapping(value = "/getUserByName",method = RequestMethod.GET)
    String getUserByName(@RequestParam(value = "name") String name);
}
