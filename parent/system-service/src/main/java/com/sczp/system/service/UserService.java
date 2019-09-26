package com.sczp.system.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
@Service
public class UserService {
    @Autowired
    RestTemplate restTemplate;

    public String getUserByName(String name) {
        try {
            Thread.sleep(5500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "{id:1,name:张三,age:25}";
    }
}
