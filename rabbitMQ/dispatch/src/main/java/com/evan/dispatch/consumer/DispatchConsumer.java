package com.evan.dispatch.consumer;

import com.alibaba.fastjson.JSONObject;
import com.evan.dispatch.entity.DispatchInfo;
import com.evan.dispatch.mapper.DispatchInfoMapper;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.junit.platform.commons.util.StringUtils;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Component
public class DispatchConsumer {
    @Autowired
    private DispatchInfoMapper dispatchInfoMapper;

    @RabbitListener(queues = "order_dic_queue")
    public void process(Message message, @Headers Map<String, Object> headers, Channel channel) throws Exception {
        String messageId = message.getMessageProperties().getMessageId();
        String msg = new String(message.getBody(), "UTF-8");
        log.info("派单服务平台" + msg + ",消息Id:" + messageId);
        JSONObject jsonObject = JSONObject.parseObject(msg);
        String orderId = jsonObject.getString("orderId");
        if (StringUtils.isBlank(orderId)) {
            //日志记录
            return;
        }
        DispatchInfo dispatchInfo = new DispatchInfo();
        dispatchInfo.setOrderId(orderId);
        dispatchInfo.setTakeoutUserId(12L);
        dispatchInfo.setDispatchRoute("40,40");
        try {
            int insertDistribute = dispatchInfoMapper.addDispatchInfo(dispatchInfo);
            if (insertDistribute > 0) {
                //手动签收消息，通知mq服务器端删除该消息
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            }
        } catch (IOException e) {
            e.printStackTrace();
            //丢弃该消息
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
        }
    }
}
