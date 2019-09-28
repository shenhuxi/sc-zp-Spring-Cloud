package com.sczp.system.service;

import com.sczp.system.entity.User;
import com.sczp.system.jpa.service.BaseService;

public interface UserService extends BaseService<User,Long> {
    User findByUserName(String teacherName);

    String getUserByName(String name);

    String testRedisKey(String redisKey);
}
