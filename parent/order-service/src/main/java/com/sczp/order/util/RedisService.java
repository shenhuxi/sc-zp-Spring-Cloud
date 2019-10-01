package com.sczp.order.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Created by on 2017/3/1.
 */
@Service
public class RedisService {

    private final RedisTemplate redisTemplate;
    @Autowired
    public RedisService(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }


    /**
     * 根据指定key获取String
     * @param key 存储的key
     * @return 库中的值  强转为String
     */
    public String getStr(String key){
        Object o = redisTemplate.opsForValue().get(key);
        if (o instanceof String){
            return (String) redisTemplate.opsForValue().get(key);
        }
        return null;
    }

    /**
     * 设置Str缓存
     * @param key 存储的key
     * @param val 存储的value
     */
    public void  setStr(String key, String val){
        redisTemplate.opsForValue().set(key,val);
    }

    /**
     * 设置Str缓存超时时间
     * @param key 存储的key
     * @param val 存储的key
     * @param timeout 过期时间
     */
    public void  setStrOut(String key, String val,long timeout){
        redisTemplate.opsForValue().set(key,val);
        redisTemplate.expire(key,timeout,TimeUnit.SECONDS);
    }

    /**
     * 设置Set缓存
     * @param key  存储的key
     * @param val 存储的set
     */
    public void  setSet(String key, Set<String> val){
        SetOperations<String, String> set = redisTemplate.opsForSet();
        for (String str: val) {
            set.add(key,str);
        }
    }

    /**
     * 获取Set缓存
     * @param key 存储的key
     */
    public Set  getSet(String key){
        return redisTemplate.opsForSet().members(key);
    }

    /**
     * 设置Map缓存
     * @param key 存储的key
     * @param val 存储的map
     */
    public void  setMap(String key, Map val){
        HashOperations map = redisTemplate.opsForHash();
        map.putAll(key,val);
    }

    /**
     * 设置Map缓存
     * @param key 存储的key
     * @param field map中的对象
     *
     */
    public Object  getMapByField(String key, String field){
        HashOperations map = redisTemplate.opsForHash();
        Object o = map.get(key, field);
        return o;
    }

    /**
     * 删除指定key
     * @param key  存储的key
     */
    public void del(String key){
        redisTemplate.delete(key);
    }

    /**
     * redis是否存在key
     * @param key 存储的key
     * @return
     */
    public Boolean haskey(String key){
        return redisTemplate.hasKey(key);
    }
    /**
     * 根据指定key获取Map
     * @param key
     * @return
     */
    public HashMap<String,Object> getMapAll(String key){
    	Map<String,Object> entries = redisTemplate.opsForHash().entries(key);
        return (HashMap<String, Object>) entries;
    }
    /**
     * 根据指定key获取Map的具体某个属性
     * @param key
     * @param property 属性
     * @return
     */
    public Object getMapProperty(String key,String property){
    	HashOperations opsForHash = redisTemplate.opsForHash();
    	return opsForHash.get(key, property);
    }
    /**
     * 管道提交
     * @param maps 集合
     */
    public void multiSet(Map<String, Object> maps) {
        redisTemplate.opsForValue().multiSet(maps);
    }
}
