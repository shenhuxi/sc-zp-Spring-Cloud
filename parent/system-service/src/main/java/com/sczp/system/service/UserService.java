package com.sczp.system.service;

import com.sczp.system.entity.User;

public interface UserService extends BaseService<User,Long> {
    User findByUserName(String teacherName);

    String getUserByName(String name);

    String testRedisKey(String redisKey);
}
