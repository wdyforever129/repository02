package com.rocketmq.rmdispatch.consumer;

import com.alibaba.fastjson.JSONObject;
import com.rocketmq.rmdispatch.model.OrderDispatchEvent;
import com.rocketmq.rmdispatch.service.IDispatchInfoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RocketMQMessageListener(consumerGroup = "producer_group_rmorder", topic = "orderRoutingKey")
public class DispatchConsumer implements RocketMQListener<String> {
    @Autowired
    private IDispatchInfoService dispatchInfoServiceImpl;

    //接受消息
    @Override
    public void onMessage(String messageString) {
        JSONObject jsonObject = JSONObject.parseObject(messageString);
        String orderDispatchEventString = jsonObject.getString("orderDispatchEvent");
        //将json转对象
        OrderDispatchEvent orderDispatchEvent = JSONObject.parseObject(orderDispatchEventString, OrderDispatchEvent.class);
        dispatchInfoServiceImpl.addDispatchInfo(orderDispatchEvent);

    }
}
