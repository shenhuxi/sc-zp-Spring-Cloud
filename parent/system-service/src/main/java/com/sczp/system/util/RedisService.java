package com.sczp.system.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisCommands;

import java.util.*;
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
    private static final Long SUCCESS = 1L;
    private final Logger logger = LoggerFactory.getLogger(RedisService.class);
    private static final String UNLOCK_LUA;

    static {
        StringBuilder sb = new StringBuilder();
        sb.append("if redis.call(\"get\",KEYS[1]) == ARGV[1] ");
        sb.append("then ");
        sb.append("    return redis.call(\"del\",KEYS[1]) ");
        sb.append("else ");
        sb.append("    return 0 ");
        sb.append("end ");
        UNLOCK_LUA = sb.toString();
    }

//----------------------------------------------分布式锁star----------------------------------------------
    /**
     * 分布式锁获取 锁
     * @param key 锁
     * @param requestId 当前线程标记 可用uuid,以value的形式存入到redis中
     * @param expire 时间  ;这里设置时间是为了防止死锁，超时自动解锁
     * @return 是否获取到
     */
    public boolean setLock(String key,String requestId, long expire) {
        try {
            // EX = seconds; PX = milliseconds 单位不同
            RedisCallback<String> callback = (connection) -> {
                JedisCommands commands = (JedisCommands) connection.getNativeConnection();
                return commands.set(key, requestId, "NX", "PX", expire);
            };
            String result = (String) redisTemplate.execute(callback);
            //不是原子操作
            //Boolean aBoolean = redisTemplate.opsForValue().setIfAbsent(key, requestId);
            //redisTemplate.expire(key,requestId,TimeUnit.SECONDS);
            return !StringUtils.isEmpty(result);
        } catch (Exception e) {
            e.getStackTrace();
        }
        return false;
    }

    public String get(String key) {
        try {
            RedisCallback<String> callback = (connection) -> {
                JedisCommands commands = (JedisCommands) connection.getNativeConnection();
                return commands.get(key);
            };
            String result;
            result = (String) redisTemplate.execute(callback);
            return result;
        } catch (Exception e) {
            logger.error("get redis occured an exception", e);
        }
        return "";
    }

    public boolean releaseLock(String key,String requestId) {
        // 释放锁的时候，有可能因为持锁之后方法执行时间大于锁的有效期，此时有可能已经被另外一个线程持有锁，所以不能直接删除
        try {
            List<String> keys = new ArrayList<>();
            keys.add(key);
            List<String> args = new ArrayList<>();
            args.add(requestId);

            // 使用lua脚本删除redis中匹配value的key，可以避免由于方法执行时间过长而redis锁自动过期失效的时候误删其他线程的锁
            // spring自带的执行脚本方法中，集群模式直接抛出不支持执行脚本的异常，所以只能拿到原redis的connection来执行脚本
            RedisCallback<Long> callback = (connection) -> {
                Object nativeConnection = connection.getNativeConnection();
                // 集群模式和单机模式虽然执行脚本的方法一样，但是没有共同的接口，所以只能分开执行
                // 集群模式
                if (nativeConnection instanceof JedisCluster) {
                    return (Long) ((JedisCluster) nativeConnection).eval(UNLOCK_LUA, keys, args);
                }

                // 单机模式
                else if (nativeConnection instanceof Jedis) {
                    Long a = (Long) ((Jedis) nativeConnection).eval(UNLOCK_LUA, keys, args);
                    return a;
                }
                return 0L;
            };
            Long result = (Long) redisTemplate.execute(callback);

            return result != null && result > 0;
        } catch (Exception e) {
            logger.error("release lock occured an exception", e);
        } finally {
            // 清除掉ThreadLocal中的数据，避免内存溢出
            //lockFlag.remove();
        }
        return false;
    }
//----------------------------------------------分布式锁end----------------------------------------------









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
        Boolean expire = redisTemplate.expire(key, timeout, TimeUnit.SECONDS);
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
