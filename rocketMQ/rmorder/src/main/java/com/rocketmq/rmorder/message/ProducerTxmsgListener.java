package com.rocketmq.rmorder.message;

import com.alibaba.fastjson.JSONObject;
import com.rocketmq.rmorder.mapper.DeDuplicationRmorderMapper;
import com.rocketmq.rmorder.model.OrderDispatchEvent;
import com.rocketmq.rmorder.service.impl.OrderTaskServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RocketMQTransactionListener(txProducerGroup = "producer_group_rmorder")
public class ProducerTxmsgListener implements RocketMQLocalTransactionListener {
    @Autowired
    private OrderTaskServiceImpl orderTaskServiceImpl;
    @Autowired
    private DeDuplicationRmorderMapper deDuplicationRmorderMapper;

    //事务消息发送后的回调方法，当消息发送给MQ成功，此方法被回调
    @Override
    @Transactional
    public RocketMQLocalTransactionState executeLocalTransaction(Message message, Object o) {

        try {
            //解析message转
            String messageString = new String((byte[]) message.getPayload());
            JSONObject jsonObject = JSONObject.parseObject(messageString);
            String orderDispatchEventString = jsonObject.getString("orderDispatchEvent");
            //将json转对象
            OrderDispatchEvent orderDispatchEvent = JSONObject.parseObject(orderDispatchEventString, OrderDispatchEvent.class);
            //执行本地事务
            orderTaskServiceImpl.addOrderAndDispatch(orderDispatchEvent);
            //当返回RocketMQLocalTransactionState.COMMIT，自动向mq发送消息，mq将消息的状态改为可消费
            return RocketMQLocalTransactionState.COMMIT;
        } catch (Exception e) {
            e.printStackTrace();
            return RocketMQLocalTransactionState.ROLLBACK;
        }
    }

    //事务状态的回查
    @Override
    public RocketMQLocalTransactionState checkLocalTransaction(Message message) {
        //解析message转
        String messageString = new String((byte[]) message.getPayload());
        JSONObject jsonObject = JSONObject.parseObject(messageString);
        Object orderDispatchEventString = jsonObject.get("orderDispatchEvent");
        //将json转对象
        OrderDispatchEvent orderDispatchEvent = JSONObject.parseObject((byte[]) orderDispatchEventString, OrderDispatchEvent.class);
        int existTx = deDuplicationRmorderMapper.isExistTx(orderDispatchEvent.getTxNo());
        if (existTx > 0) {//发送消息成功
            return RocketMQLocalTransactionState.COMMIT;
        } else {//消息未发送出去，未知什么情况，可能网络不好，等网络好了可能还会继续发送消息
            return RocketMQLocalTransactionState.UNKNOWN;
        }
    }
}
