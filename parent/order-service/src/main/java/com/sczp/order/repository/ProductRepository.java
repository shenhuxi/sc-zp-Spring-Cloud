package com.sczp.order.repository;

import com.sczp.order.entity.Product;
import com.sczp.order.jpa.repository.BaseRepository;
import org.springframework.stereotype.Repository;

/**
 * 描述:
 * 作者: qinzhw
 * 创建时间: 2018/8/14 14:42
 */
@Repository
public interface ProductRepository extends BaseRepository<Product,Long> {

    Product findBySerialNumber(String SerialNumber);
}