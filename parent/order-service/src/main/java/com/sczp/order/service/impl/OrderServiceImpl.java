package com.sczp.order.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.sczp.order.config.RabbitConfig;
import com.sczp.order.entity.EventPublish;
import com.sczp.order.entity.Order;
import com.sczp.order.feign_service.SystemApi;
import com.sczp.order.jpa.repository.BaseRepository;
import com.sczp.order.jpa.service.impl.BaseServiceImpl;
import com.sczp.order.moudl.EventStatus;
import com.sczp.order.moudl.EventType;
import com.sczp.order.repository.EventPublishRepository;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.concurrent.ExecutorService;

@Service
public class OrderServiceImpl extends BaseServiceImpl<Order,Long> implements OrderService , RabbitTemplate.ConfirmCallback  {
    ExecutorService threadPoolInstance = ThreadPoolInstance.getThreadPoolInstance();
    private final SystemApi systemApi;
    private final RedisService redisService;
    private final RedisTemplate redisTemplate;
    private final OrderRepository orderRepository;
    private final EventPublishRepository eventPublishRepository;
    //由于rabbitTemplate的scope属性设置为ConfigurableBeanFactory.SCOPE_PROTOTYPE，所以不能自动注入
    private  EventPublish event ;
    private  CorrelationData correlationId;
    private  String strJson;

    private RabbitTemplate rabbitTemplate;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public OrderServiceImpl(SystemApi systemApi, RedisService redisService,
                            RedisTemplate redisTemplate, OrderRepository orderRepository,
                            EventPublishRepository eventPublishRepository, RabbitTemplate rabbitTemplate) {
        this.systemApi = systemApi;
        this.redisService = redisService;
        this.redisTemplate = redisTemplate;
        this.orderRepository = orderRepository;
        this.eventPublishRepository = eventPublishRepository;
        this.rabbitTemplate = rabbitTemplate;
        //rabbitTemplate如果为单例的话，那回调就是最后设置的内容
        //rabbitTemplate设置回调：确保发送到队列没
        rabbitTemplate.setConfirmCallback(this);
    }

    /**
     * 1.实现：这个更像是mq的数据确保机制.................
     * 2.分布式事务，用到mq的回调方法
     * @param order 订单
     * @return 订单
     */
    @Transactional(rollbackFor = Exception.class)
    public  boolean  createOrder(Order order){
        //step1. 创建订单
        getCommonRepository().save(order);

        String eventId = UUID.randomUUID().toString();
        JSONObject object = new JSONObject();
        object.put("orderCode", order.getOrderCode());
        object.put("price", order.getPrice());
        object.put("userId", order.getUserId());
        object.put("eventId", eventId);
        strJson =object.toJSONString();

        //step2. 创建事件对象---提交一次事务
        this.event = new EventPublish();
        event.setEventID(eventId);
        event.setStatus(EventStatus.NEW);
        event.setEventType(EventType.ORDER_PAY);
        event.setPayload(strJson);
        eventPublishRepository.save(event);
        eventPublishRepository.flush();

        //step3. 发送减去用户资产 的请求给System服务-----到队列
        correlationId = new CorrelationData(eventId);
        sendQueen(strJson,correlationId);
        return true;
    }

    private void sendQueen(String strJson, CorrelationData correlationId){
        rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE_Order_Pay, RabbitConfig.ROUTINGKEY_Order_Pay, strJson, correlationId);
    }

    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        System.out.println(" 回调id:" + correlationData);
        int numb = 0;
        while (numb<5){//尝试5次失败，记录发送失败；人工处理
            if (ack) {
                System.out.println("消息成功发布到rabbitMQ");
                event.setStatus(EventStatus.PUBLISHED);
                break;
            } else {
                sendQueen(strJson,correlationId);
                try {
                    Thread.sleep(100);
                    numb++;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        if (numb>=5){
            event.setStatus(EventStatus.PUBLISHED_FAIL);
        }
        eventPublishRepository.save(event);
    }

    public String testRedisKey(String redisKey) {//在system测试了  这里不完善了
        String systemResult = systemApi.testRedisKey("张三");

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
