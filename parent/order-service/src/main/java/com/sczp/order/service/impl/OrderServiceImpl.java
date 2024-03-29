package com.sczp.order.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.sczp.common.aspect.SystemLog;
import com.sczp.common.exception.DataNotFoundException;
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
import com.sczp.order.util.threadPool.ThreadPoolInstance;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

@Service
public class OrderServiceImpl extends BaseServiceImpl<Order,Long> implements OrderService , RabbitTemplate.ConfirmCallback  {
    Logger logger =  LogManager.getLogger("weather");

    ExecutorService threadPoolInstance = ThreadPoolInstance.getThreadPoolInstance();
    private final SystemApi systemApi;
    private final RedisService redisService;
    private final RedisTemplate<String,String> redisTemplate;
    private final OrderRepository orderRepository;
    private final EventPublishRepository eventPublishRepository;
    //由于rabbitTemplate的scope属性设置为ConfigurableBeanFactory.SCOPE_PROTOTYPE，所以不能自动注入

    private RabbitTemplate rabbitTemplate;

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
    @SystemLog(dataType = "订单创建")
    public  boolean  createOrder(Order order){
        //step1. 创建订单
        getCommonRepository().save(order);
        threadPoolInstance.execute(()->{
            String eventId = UUID.randomUUID().toString();
            JSONObject object = new JSONObject();
            object.put("orderCode", order.getOrderCode());
            object.put("price", order.getPrice());
            object.put("userId", order.getUserId());
            object.put("eventId", eventId);
            String strJson =object.toJSONString();

            //step2. 创建事件对象---提交一次事务
            EventPublish event = new EventPublish();
            event.setEventID(eventId);
            event.setStatus(EventStatus.NEW);
            event.setEventType(EventType.ORDER_PAY);
            event.setPayload(strJson);
            eventPublishRepository.save(event);
            eventPublishRepository.flush();

            //step3. 发送减去用户资产 的请求给System服务-----到队列
            CorrelationData correlationId = new CorrelationData(eventId);
            sendQueen(strJson,correlationId);
        });
        return true;
    }

    @Override
    @SystemLog(dataType = "订单查询")
    public Order findOrderById(Long id) {
        Optional<Order> byId = this.orderRepository.findById(id);
        Order order= byId.orElseThrow(() -> new DataNotFoundException(id.toString()));
        return order;
    }

    private void sendQueen(String strJson, CorrelationData correlationId){
        rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE_Order_Pay, RabbitConfig.ROUTINGKEY_Order_Pay, strJson, correlationId);
    }

    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        System.out.println(" 回调id:" + correlationData);
        int numb = 0;
        if (ack) {
            System.out.println("消息成功发布到rabbitMQ");
        } else {
            System.out.println("消息发布到rabbitMQ失败");
        }
    }

    public String testRedisKey(String redisKey) {//在system测试了  这里不完善了
        //String systemResult = systemApi.testRedisKey("张三");
        redisTemplate.opsForValue().set("aasd","a收到");
        //step1 处理逻辑 business.....
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return "msg:order处理成功,code:200 | ";
    }

    @Override
    public BaseRepository<Order, Long> getCommonRepository() {
        return this.orderRepository;
    }
}
