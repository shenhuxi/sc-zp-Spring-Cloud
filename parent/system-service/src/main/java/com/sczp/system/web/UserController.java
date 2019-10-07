package com.sczp.system.web;

import com.sczp.system.entity.User;
import com.sczp.system.exception.DataNotFoundException;
import com.sczp.system.jpa.utils.MD;
import com.sczp.system.jpa.utils.ResultObject;
import com.sczp.system.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.concurrent.locks.ReentrantLock;

@RestController
@RequestMapping("/user")
public class UserController {
    private Integer number = 0;

    private final UserService userService;

    ReentrantLock lock = new ReentrantLock();

    @Value("${adminName}")
    String adminName;
    @Value("${adminPassword}")
    String adminPassword;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/add")//测试Jpa
    public ResultObject<?> add(@Valid @RequestBody User user, BindingResult br) throws DataNotFoundException {
        if (br.hasErrors()) {
            //getValidErrorMsg(br)
            return ResultObject.error("参数错误");
        }
        String initPassword = "f883ee10adc3949ba59abbe56e057f20";
        user.setPassWord(MD.md5(initPassword +user.getUserName()));
        User findByUserName1 = userService.findByUserName(user.getUserName());
        User findByUserName = userService.findOne(2L);
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

    @GetMapping("/testSynchronized")//测试controller是单线程还是多线程
    public String testSynchronized(){
        //1.不加锁会出错
        for (int i = 0; i < 100; i++) {
            this.number=this.number+1;

        }
        try {
            Thread.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(Thread.currentThread().getName());
        return number.toString();

        //2.synchronized
        /*synchronized (this.number){
            for (int i = 0; i < 100; i++) {
                this.number=this.number+1;
            }
            return number.toString();
        }*/

        //3.lock
        /*try {
            while(true){
                if(lock.tryLock()){
                    for (int i = 0; i < 100; i++) {
                        this.number=this.number+1;
                    }
                    break;
                }
            }
            return number.toString();
        } finally {
            lock.unlock();
        }*/
    }
    @GetMapping("/getResultValue")//测试controller是单线程还是多线程  :获取累加后的值
    public Integer getResultValue(){
        return number=0;
        //userService.testSynchronized();
    }


}
