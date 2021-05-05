package com.besteffortnotify.rmbenpays.service.impl;

import com.besteffortnotify.rmbenpays.entity.AccountPay;
import com.besteffortnotify.rmbenpays.mapper.AccountPayMapper;
import com.besteffortnotify.rmbenpays.service.IAccountInfoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class AccountPayServiceImpl implements IAccountInfoService {
    @Autowired
    private AccountPayMapper accountPayMapper;
    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    /**
     * 插入充值记录
     */
    @Override
    @Transactional
    public AccountPay insertAccountPay(AccountPay accountPay) {
        accountPay.setStatus("SUCCESS");
        int insertResult = accountPayMapper.insert(accountPay);
        if (insertResult > 0) {
            //采用普通消息实现:尽最大努力通知，如果通知不到可以来查询
            rocketMQTemplate.convertAndSend("topic_rmbenpays", accountPay);
            return accountPay;
        }
        return null;
    }

    /**
     * 查询充值记录：接收通知方调用此方法来查询充值结果
     */
    @Override
    public AccountPay getById(String txNo) {
        return accountPayMapper.getById(txNo);
    }
}
