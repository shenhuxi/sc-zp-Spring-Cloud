package com.sczp.system.web;

import com.sczp.system.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @Value("${adminName}")
    String adminName;
    @Value("${adminPassword}")
    String adminPassword;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/testRedisKey")
    public String testRedisKey(String redisKey){
        return userService.testRedisKey(redisKey);
    }

    @GetMapping("/getUserByName")
    public String getUserByName(String name){
        return userService.getUserByName(name);
    }

    @GetMapping("/getAdminInfo")
    public String getAdminInfo(String name){
        return this.adminName+":"+this.adminPassword;
    }


}
