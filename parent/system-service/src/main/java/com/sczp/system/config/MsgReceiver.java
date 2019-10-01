package com.sczp.system.config;

import com.rabbitmq.client.Channel;
import com.sczp.order.util.JSONUtils;
import com.sczp.system.entity.EventProcess;
import com.sczp.system.moudl.EventStatus;
import com.sczp.system.moudl.EventType;
import com.sczp.system.repository.EventProcessRepository;
import com.sczp.system.util.RedisService;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Component
public class MsgReceiver {
    private final EventProcessRepository eventProcessRepository;
    private final RedisService redisService;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public MsgReceiver(EventProcessRepository eventProcessRepository, RedisService redisService) {
        this.eventProcessRepository = eventProcessRepository;
        this.redisService = redisService;
    }


    @RabbitListener(queues = RabbitConfig.QUEUE_Order_Pay)
    @Transactional(rollbackFor = Exception.class)
    public void process_A_One(Message message, Channel channel) throws IOException, InterruptedException {
        //step1. 确认收到了
        byte[] body = message.getBody();
        JSONObject jsonObject = JSONUtils.toJSONObject(new String(body));
        //判断是否重复消费--redis中间件

        String eventId = null;
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

        EventProcess eventProcess = new EventProcess();
        eventProcess.setEventID(eventId);
        eventProcess.setStatus(EventStatus.NEW);
        eventProcess.setEventType(EventType.ORDER_PAY);
        eventProcess.setPayload(jsonObject.toString());
        eventProcessRepository.save(eventProcess);
       // channel.basicAck(message.getMessageProperties().getDeliveryTag(), true);
        eventProcessRepository.flush();

        //step2. 处理逻辑
        System.out.println("消费:接收处理队列QUEUE_Order_Pay当中的消息： "+jsonObject.toString());
        Thread.sleep(1000);
        eventProcess.setStatus(EventStatus.PUBLISHED);
    }


}