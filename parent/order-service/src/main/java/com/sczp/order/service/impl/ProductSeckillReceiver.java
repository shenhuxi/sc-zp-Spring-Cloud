package com.sczp.order.service.impl;

import com.rabbitmq.client.Channel;
import com.sczp.order.config.RabbitConfig;
import com.sczp.order.entity.Order;
import com.sczp.order.repository.OrderRepository;
import com.sczp.order.util.JSONUtils;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.UUID;

@Component
public class ProductSeckillReceiver {
    private final RedisService redisService;
    private final OrderRepository orderRepository;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public ProductSeckillReceiver(RedisService redisService, OrderRepository orderRepository) {
        this.redisService = redisService;
        this.orderRepository = orderRepository;
    }

    /**
     * 秒杀服务将事件发送到了订单服务
     * @param message
     * @param channel
     * @throws IOException
     * @throws InterruptedException
     */
    @RabbitListener(queues = RabbitConfig.QUEUE_Product_Seckill)
    @Transactional(rollbackFor = Exception.class)
    public void process_A_One(Message message, Channel channel) throws IOException, InterruptedException {
        //step1. 确认收到了
        byte[] body = message.getBody();
        JSONObject jsonObject = JSONUtils.toJSONObject(new String(body));

        //step2. 判断是否重复消费--redis中间件
        String eventId ;
        try {
            eventId = jsonObject.getString("eventId");
        } catch (Exception e) {
            return;
        }
        if(redisService.getStr("QUEUE_Order_Pay:"+eventId)==null){
            redisService.setStrOut(eventId,null,60);
        }else {
            //channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            return;
        }

        //step3. 处理逻辑;创建相应订单
        System.out.println("消费:接收处理队列QUEUE_Order_Pay当中的消息： "+jsonObject.toString());
        Order order =  new Order();
        order.setOrderCode(UUID.randomUUID().toString());
        order.setUserId(Long.parseLong(jsonObject.get("userId").toString()));
        order.setPrice(new BigDecimal(jsonObject.get("price").toString()));
        order.setSerialNumber(jsonObject.get("serialNumber").toString());
        orderRepository.save(order);
    }


}