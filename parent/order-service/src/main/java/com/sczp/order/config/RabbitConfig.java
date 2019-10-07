package com.sczp.order.config;

import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;


/**
 * Created by zengpeng on 2019/3/30
 */
@Configuration
public class RabbitConfig {


    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${spring.rabbitmq.host}")
    private String host;

    @Value("${spring.rabbitmq.port}")
    private int port;

    @Value("${spring.rabbitmq.username}")
    private String username;

    @Value("${spring.rabbitmq.password}")
    private String password;

//----------------------------------------参数star--------------------------------------------
    public static final String EXCHANGE_Order_Pay = "my-mq-exchange_Order_Pay";
    public static final String EXCHANGE_Product_Seckill= "my-mq-exchange_Product_Seckill";

    public static final String QUEUE_Order_Pay = "QUEUE_Order_Pay";
    public static final String QUEUE_Product_Seckill = "QUEUE_Product_Seckill";

    public static final String ROUTINGKEY_Order_Pay = "spring-boot-routingKey_Order_Pay";
    public static final String ROUTINGKEY_Product_Seckill = "spring-boot-routingKey_Product_Seckill";
    //----------------------------------------参数end--------------------------------------------

    //----------------------------------------配置star--------------------------------------------
    @Bean
    public CachingConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory(host,port);
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);
        connectionFactory.setVirtualHost("/");
        connectionFactory.setPublisherConfirms(true);
        return connectionFactory;
    }


    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    //必须是prototype类型
    public RabbitTemplate rabbitTemplate() {
        RabbitTemplate template = new RabbitTemplate(connectionFactory());
        //template.setMessageConverter(new Jackson2JsonMessageConverter());
        return template;
    }
//----------------------------------------配置end--------------------------------------------


//----------------------------------------配置交换机、队列、路由 star--------------------------------------------
    /**
     * 针对消费者配置
     * 1. 设置交换机类型
     * 2. 将队列绑定到交换机
     FanoutExchange: 将消息分发到所有的绑定队列，无routingkey的概念
     HeadersExchange ：通过添加属性key-value匹配
     DirectExchange:按照routingkey分发到指定队列
     TopicExchange:多关键字匹配
     */
    @Bean
    public DirectExchange defaultExchange() {
        return new DirectExchange(EXCHANGE_Order_Pay);
    }
    @Bean
    public DirectExchange productSeckillExchange() {
        return new DirectExchange(EXCHANGE_Product_Seckill);
    }
    /**
     * 获取队列A B C
     */
    @Bean
    public Queue queue_Order_Pay() {
        return new Queue(QUEUE_Order_Pay, true); //队列持久
    }
    @Bean
    public Queue queue_Product_Seckill() {
        return new Queue(QUEUE_Product_Seckill, true); //队列持久
    }

    @Bean
    public Binding binding() {
        return BindingBuilder.bind(queue_Order_Pay()).to(defaultExchange()).with(RabbitConfig.ROUTINGKEY_Order_Pay);
    }
    @Bean
    public Binding bindingProductSeckill() {
        return BindingBuilder.bind(queue_Product_Seckill()).to(productSeckillExchange()).with(RabbitConfig.ROUTINGKEY_Product_Seckill);
    }
    //----------------------------------------配置交换机、队列、路由 end--------------------------------------------



    //-----------------------配置广播star--------------------------------------------
    // Fanout 就是我们熟悉的广播模式，给Fanout交换机发送消息，绑定了这个交换机的所有队列都收到这个消息。
    //配置fanout_exchange
    // @Bean
    /*FanoutExchange fanoutExchange() {
        return new FanoutExchange(RabbitConfig.FANOUT_EXCHANGE);
    }*/

    // @Bean
    Binding bindingExchangeB(Queue queueB, FanoutExchange fanoutExchange) {
        return BindingBuilder.bind(queueB).to(fanoutExchange);
    }
    // @Bean
    Binding bindingExchangeC(Queue queueC, FanoutExchange fanoutExchange) {
        return BindingBuilder.bind(queueC).to(fanoutExchange);
    }
    //-----------------------配置广播end--------------------------------------------



    //注: 一个消费者处理多个队列
    //@Bean
    public SimpleMessageListenerContainer messageContainer() {
        //加载处理消息A的队列
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory());
        //设置接收多个队列里面的消息，这里设置接收队列A
        //假如想一个消费者处理多个队列里面的信息可以如下设置：
        //container.setQueues(queueA(),queueB(),queueC());
        container.setQueues(queue_Order_Pay());
        container.setExposeListenerChannel(true);
        //设置最大的并发的消费者数量
        container.setMaxConcurrentConsumers(10);
        //最小的并发消费者的数量
        container.setConcurrentConsumers(1);
        //设置确认模式手工确认
        container.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        container.setMessageListener(new ChannelAwareMessageListener() {
            @Override
            public void onMessage(Message message, Channel channel) throws Exception {
                /**通过basic.qos方法设置prefetch_count=1，这样RabbitMQ就会使得每个Consumer在同一个时间点最多处理一个Message，
                 换句话说,在接收到该Consumer的ack前,它不会将新的Message分发给它 */
                channel.basicQos(1);
                byte[] body = message.getBody();
                logger.info("接收处理队列A、B当中的消息:" + new String(body));
                /**为了保证永远不会丢失消息，RabbitMQ支持消息应答机制。
                 当消费者接收到消息并完成任务后会往RabbitMQ服务器发送一条确认的命令，然后RabbitMQ才会将消息删除。*/
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            }
        });
        return container;
    }
}
