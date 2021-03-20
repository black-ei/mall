package com.baidu.rabbitmq;

import com.baidu.shop.constant.MqMessageConstant;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {
    /*
    * 创建延时队列
    * "x-dead-letter-exchange"参数定义死信队列交换机
    * "x-dead-letter-routing-key"定义死信队列中的消息重定向时的routing-key
    * "x-message-ttl"定义消息的过期时间
    * */

    @Bean
    public Queue delayQueue(){
        return QueueBuilder.durable(MqMessageConstant.DELAY_QUEUE)
                .withArgument("x-dead-letter-exchange",MqMessageConstant.DELAY_EXCHANGE)
                .withArgument("x-dead-letter-routing-key",MqMessageConstant.BIZ_QUEUE)
                .withArgument("x-message-ttl",MqMessageConstant.DELAY_TTL)
                .build();
    }

    /**创建用于业务的队列*/
    @Bean
    public Queue bizQueue(){
        return QueueBuilder.durable(MqMessageConstant.BIZ_QUEUE).build();
    }

    /**创建一个DirectExchange*/
    @Bean
    public DirectExchange delayExchange(){
        return new DirectExchange(MqMessageConstant.DELAY_EXCHANGE);
    }

    /**绑定Exchange和queue，把消息重定向到业务queue*/
    @Bean
    public Binding dlxBinding(DirectExchange directExchange, Queue bizQueue){
        return BindingBuilder.bind(bizQueue)
                .to(directExchange)
                .with(MqMessageConstant.BIZ_QUEUE);
    }
}
