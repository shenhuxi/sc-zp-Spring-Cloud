package com.sczp.system.repository;

import com.sczp.system.entity.User;
import org.springframework.stereotype.Repository;

/**
 * 描述:
 * 作者: qinzhw
 * 创建时间: 2018/8/14 14:42
 */
@Repository
public interface UserRepository extends BaseRepository<User,Long> {
    User findByUserName(String teacherName);
}