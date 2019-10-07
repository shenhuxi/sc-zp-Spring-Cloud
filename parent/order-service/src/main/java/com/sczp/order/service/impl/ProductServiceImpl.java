package com.sczp.order.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.sczp.order.config.RabbitConfig;
import com.sczp.order.entity.Product;
import com.sczp.order.repository.ProductRepository;
import com.sczp.order.service.ProductService;
import com.sczp.order.util.ObjectToMap;
import com.sczp.order.util.threadPool.ThreadPoolInstance;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

@Service
public class ProductServiceImpl implements ProductService , RabbitTemplate.ConfirmCallback{
    private final RedisService redisService;
    private final ProductRepository productRepository;
    private final RedisTemplate redisTemplate;
    private final RabbitTemplate rabbitTemplate;

    ExecutorService threadPoolInstance = ThreadPoolInstance.getThreadPoolInstance();

    private final String seckillProduct_Key = "seckillProduct";
    private final String seckillProduct_RedisLock_Key = "seckillProduct";

    @Autowired
    public ProductServiceImpl(RedisService redisService, ProductRepository productRepository,
                              RedisTemplate redisTemplate, RabbitTemplate rabbitTemplate) {
        this.redisService = redisService;
        this.productRepository = productRepository;
        this.redisTemplate = redisTemplate;
        this.rabbitTemplate = rabbitTemplate;
        this.rabbitTemplate.setConfirmCallback(this);
    }

    @Override
    @Transactional
    public boolean seckillProduct(String SerialNumber, Integer number, Long userId) {
        //step1. 获得分布式事务锁
        while(true){
            String eventId = UUID.randomUUID().toString();
            boolean setKeyIsOK = redisService.setLock(seckillProduct_RedisLock_Key+SerialNumber, eventId, 5000);

        //step2. 查询商品缓存，没有则查询DB,将其放入缓存，设置存活时间300秒
            if(setKeyIsOK){
                try {
                    HashOperations hashOperations = redisTemplate.opsForHash();
                    Map<String,String> map = hashOperations.entries(seckillProduct_Key + SerialNumber);

                    if(map.isEmpty()){
                        Product targetProduct = productRepository.findBySerialNumber(SerialNumber);
                        if (targetProduct==null)
                            return false;
                        map = ObjectToMap.getNamValMap(targetProduct, false);
                    }
                    if(map.get("stocks")==null)
                        return false;
                    Long stocks = Long.parseLong(map.get("stocks"));

                    stocks = stocks-number;
                    if(stocks>=0){
                        map.put("stocks",stocks.toString());
        //step3. 商品库存减一,修改库存stocks，缓存进入redis,释放锁
                        hashOperations.putAll(seckillProduct_Key + SerialNumber,map);
        //step5. 创建订单事件，发送到队列，并且存储一条关于队列的数据，以防止发送失败时的重发
                        sendMQ(userId, map);

        //step4. 用线程池去更新DB
                        threadPoolInstance.execute(()->{
                            Product targetProduct = productRepository.findBySerialNumber(SerialNumber);
                            HashOperations hashOperations2 = redisTemplate.opsForHash();
                            Map<String,String> map2 = hashOperations.entries(seckillProduct_Key + SerialNumber);
                            if(!map2.isEmpty() && map2.get("stocks")!=null){
                                targetProduct.setStocks(Long.parseLong(map2.get("stocks")));
                            }
                            productRepository.save(targetProduct);
                        });
                        break;
                    }
                    else
                        return false;
                } finally {//释放锁
                    redisService.releaseLock(seckillProduct_RedisLock_Key+SerialNumber,eventId);
                }
            }else {
                try {
                    Thread.sleep(2);
                    System.out.println("当前线程:"+Thread.currentThread().getName()+" 等待2毫秒");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }


        return true;
    }

    private void sendMQ(Long userId, Map<String, String> map) {
        String rabbitMQId = UUID.randomUUID().toString();
        JSONObject object = new JSONObject();
        object.put("serialNumber", map.get("serialNumber"));
        object.put("price", map.get("price"));
        object.put("userId", userId);
        object.put("eventId", rabbitMQId);
        String strJson =object.toJSONString();
        CorrelationData correlationId = new CorrelationData(rabbitMQId);
        rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE_Product_Seckill, RabbitConfig.ROUTINGKEY_Product_Seckill, strJson, correlationId);
    }

    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        if (ack) {
            System.out.println("秒杀信息成功发布到rabbitMQ");
            //通过回调id==correlationData 修改db信息为成功发布
        } else {
            System.out.println("消息发布到rabbitMQ失败");
            //通过回调id==correlationData  查询需要发送到队列的数据，重新发布
        }
    }
}
