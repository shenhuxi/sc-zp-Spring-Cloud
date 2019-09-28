package com.sczp.system.web;

import com.sczp.system.entity.User;
import com.sczp.system.jpa.utils.MD;
import com.sczp.system.jpa.utils.ResultObject;
import com.sczp.system.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

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
    @PostMapping("/add")//测试Jpa
    public ResultObject<?> add(@Valid @RequestBody User user, BindingResult br){
        if (br.hasErrors()) {
            //getValidErrorMsg(br)
            return ResultObject.error("参数错误");
        }
        String initPassword = "f883ee10adc3949ba59abbe56e057f20";
        user.setPassWord(MD.md5(initPassword +user.getUserName()));
        User findByUserName1 = userService.findByUserName(user.getUserName());
        User findByUserName = userService.findOne(1L);
        if(findByUserName!=null||findByUserName1!=null) {
            return ResultObject.error("账号已存在！");
        }
        User saveUser = userService.save(user);
        return ResultObject.ok(saveUser);
    }

    @GetMapping("/testRedisKey")//测试分布式锁
    public String testRedisKey(String redisKey){
        return userService.testRedisKey(redisKey);
    }

    @GetMapping("/getUserByName")//测试figen  以及hystrix熔断
    public String getUserByName(String name){
        return userService.getUserByName(name);
    }

    @GetMapping("/getAdminInfo")//测试confg
    public String getAdminInfo(String name){
        return this.adminName+":"+this.adminPassword;
    }




}
