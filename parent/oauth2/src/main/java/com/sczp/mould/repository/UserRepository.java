package com.sczp.mould.repository;

import com.sczp.jpa.repository.BaseRepository;
import com.sczp.mould.entity.User;
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