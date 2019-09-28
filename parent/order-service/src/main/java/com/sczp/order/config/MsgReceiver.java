package com.sczp.order.config;

import com.sczp.order.util.JSONUtils;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class MsgReceiver {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    @RabbitListener(queues = RabbitConfig.QUEUE_Order_Pay)
    public void process_A_One(Message message){
        byte[] body = message.getBody();
        JSONObject jsonObject = JSONUtils.toJSONObject(new String(body));
        System.out.println("消费:接收处理队列QUEUE_Order_Pay当中的消息： "+jsonObject.toString());
    }

}