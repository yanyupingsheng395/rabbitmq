package com.tuling.config;

import com.tuling.constants.MqConst;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
* @desc: 类的描述:rabbitmq相关的配置
* @version: 1.0
 *
*/
@Configuration
public class RabbitmqConfig {

    /**
     * 声明交换机
     * @return
     */
    @Bean
    public DirectExchange orderToProductExchange() {
        DirectExchange directExchange = new DirectExchange(MqConst.ORDER_TO_PRODUCT_EXCHANGE_NAME,true,false);
        return directExchange;
    }

    /**
     * 声明队列
     * @return
     */
    @Bean
    public Queue orderToProductQueue() {
        Queue queue = new Queue(MqConst.ORDER_TO_PRODUCT_QUEUE_NAME,true,false,false);
        return queue;
    }

    /**
     * 交换机绑定队列
     * @return
     */
    @Bean
    public Binding orderToProductBinding() {
        return BindingBuilder.bind(orderToProductQueue()).to(orderToProductExchange()).with(MqConst.ORDER_TO_PRODUCT_ROUTING_KEY);
    }
}
