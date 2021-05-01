package com.distributed.order.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class config {
    //派单队列
    public static final String ORDER_DIC_QUEUE = "order_dic_queue";
    //补单队列，判断订单是否已经被创建
    public static final String ORDER_CREATE_QUEUE = "order_create_queue";

    //派单路由key
    public static final String ORDER_ROUTING_KEY = "orderRoutingKey";
    //补单路由key
    public static final String ORDER_CREATE_ROUTING_KEY = "orderCreateRoutingKey";

    //派单交换机
    private static final String ORDER_EXCHANGE_NAME = "order_exchange_name";

    //定义派单队列
    @Bean(ORDER_DIC_QUEUE)
    public Queue directOrderDicQueue(){
        return new Queue(ORDER_DIC_QUEUE);
    }

    //定义补单队列
    @Bean(ORDER_CREATE_QUEUE)
    public Queue directCreateOrderQueue(){
        return new Queue(ORDER_CREATE_QUEUE);
    }

    //定义交换机
    @Bean(ORDER_EXCHANGE_NAME)
    DirectExchange directOrderExchange(){
        return new DirectExchange(ORDER_EXCHANGE_NAME);
    }

    //派单队列绑定到交换机
    @Bean
    Binding bindingExchangeDirectOrderDic(@Qualifier(ORDER_DIC_QUEUE) Queue queue, @Qualifier(ORDER_EXCHANGE_NAME)Exchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with(ORDER_ROUTING_KEY).noargs();
    }

    //补单队列绑定到交换机
    @Bean
    Binding bindingExchangeDirectCreateOrder(@Qualifier(ORDER_CREATE_QUEUE) Queue queue, @Qualifier(ORDER_EXCHANGE_NAME)Exchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with(ORDER_CREATE_ROUTING_KEY).noargs();
    }
}
