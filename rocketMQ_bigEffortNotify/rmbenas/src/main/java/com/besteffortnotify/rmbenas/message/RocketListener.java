package com.besteffortnotify.rmbenas.message;

import com.alibaba.fastjson.JSONObject;
import com.besteffortnotify.rmbenas.entity.AccountPay;
import com.besteffortnotify.rmbenas.model.AccountChangeEvent;
import com.besteffortnotify.rmbenas.service.impl.AccountInfoServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RocketMQMessageListener(consumerGroup = "consumer_rmbenas", topic = "topic_rmbenpays")
public class RocketListener implements RocketMQListener<AccountPay> {
    @Autowired
    private AccountInfoServiceImpl accountInfoService;

    @Override
    public void onMessage(AccountPay accountPay) {
        log.info("接收到消息：{}", JSONObject.toJSONString(accountPay));
        //更新账户金额
        if ("SUCCESS".equals(accountPay.getStatus())) {
            AccountChangeEvent accountChangeEvent = new AccountChangeEvent();
            accountChangeEvent.setAccountNo(accountPay.getAccountNo());
            accountChangeEvent.setAmount(accountPay.getPayAmount());
            accountChangeEvent.setTxNo(accountPay.getId());
            accountChangeEvent.setUpdateTime(accountPay.getUpdateTime());
            accountInfoService.updateAccountBalance(accountChangeEvent);
        }
    }
}
