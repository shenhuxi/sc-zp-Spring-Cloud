package com.sczp.order.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.sczp.order.config.RabbitConfig;
import com.sczp.order.entity.Order;
import com.sczp.order.feign_service.SystemApi;
import com.sczp.order.jpa.repository.BaseRepository;
import com.sczp.order.jpa.service.impl.BaseServiceImpl;
import com.sczp.order.repository.OrderRepository;
import com.sczp.order.service.OrderService;
import com.sczp.order.util.RedisService;
import com.sczp.order.util.threadPool.ThreadPoolInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.ExecutorService;

@Service
public class OrderServiceImpl extends BaseServiceImpl<Order,Long> implements OrderService   {
    private final SystemApi systemApi;
    private final RedisService redisService;
    private final RedisTemplate redisTemplate;
    private final OrderRepository orderRepository;
    //由于rabbitTemplate的scope属性设置为ConfigurableBeanFactory.SCOPE_PROTOTYPE，所以不能自动注入
    private RabbitTemplate rabbitTemplate;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public OrderServiceImpl(SystemApi systemApi, RedisService redisService,
                            RedisTemplate redisTemplate, OrderRepository orderRepository,
                            RabbitTemplate rabbitTemplate) {
        this.systemApi = systemApi;
        this.redisService = redisService;
        this.redisTemplate = redisTemplate;
        this.orderRepository = orderRepository;
        this.rabbitTemplate = rabbitTemplate;
        //rabbitTemplate如果为单例的话，那回调就是最后设置的内容
    }

    /**
     * 1.实现：这个更像是mq的数据确保机制.................
     * 2.分布式事务，用到mq的回调方法
     * @param order 订单
     * @return 订单
     */
    public  boolean  createOrder(Order order){
        //step1. 创建订单
        getCommonRepository().save(order);

        //step2. 发送减去用户资产 的请求给System服务-----到队列
        CorrelationData correlationId = new CorrelationData(UUID.randomUUID().toString());
        JSONObject object = new JSONObject();
        object.put("orderCode", order.getOrderCode());
        object.put("price", order.getPrice());
        object.put("userId", order.getUserId());
        String strJson =object.toJSONString();
        //rabbitTemplate设置回调：确保发送到队列没
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            System.out.println(" 回调id:" + correlationData);
            if (ack) {
                System.out.println("消息成功消费");
            } else {
                System.out.println("消息消费失败:" + cause);
            }
        });
        //step3.把消息放入队列 QUEUE_Order_Pay
        rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE_Order_Pay, RabbitConfig.ROUTINGKEY_Order_Pay, strJson, correlationId);
        return true;
    }

    public String testRedisKey(String redisKey) {//在system测试了  这里不完善了
        String systemResult = systemApi.testRedisKey("张三");
        ExecutorService threadPoolInstance = ThreadPoolInstance.getThreadPoolInstance();
        //step1 处理逻辑 business.....
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return "msg:order处理成功,code:200 | "+systemResult;
    }

    @Override
    public BaseRepository<Order, Long> getCommonRepository() {
        return this.orderRepository;
    }
}
