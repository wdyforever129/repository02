package com.besteffortnotify.rmbenas.service.impl;

import com.besteffortnotify.rmbenas.entity.AccountInfo;
import com.besteffortnotify.rmbenas.entity.AccountPay;
import com.besteffortnotify.rmbenas.entity.DeDuplicationRmbenas;
import com.besteffortnotify.rmbenas.mapper.AccountInfoMapper;
import com.besteffortnotify.rmbenas.mapper.DeDuplicationRmbenasMapper;
import com.besteffortnotify.rmbenas.model.AccountChangeEvent;
import com.besteffortnotify.rmbenas.remote.RmbenPaysClient;
import com.besteffortnotify.rmbenas.service.IAccountInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;

@Slf4j
@Service
public class AccountInfoServiceImpl implements IAccountInfoService {
    @Autowired
    private AccountInfoMapper accountInfoMapper;
    @Autowired
    private DeDuplicationRmbenasMapper deDuplicationRmbenasMapper;
    @Autowired
    private RmbenPaysClient rmbenPaysClient;

    @Override
    @Transactional
    public void updateAccountBalance(AccountChangeEvent accountChangeEvent) {
        String id = accountChangeEvent.getTxNo();
        //幂等校验
        int existTx = deDuplicationRmbenasMapper.isExistTx(id);
        if (existTx > 0) {
            return;
        }

        String accountNo = accountChangeEvent.getAccountNo();
        BigDecimal amount = accountChangeEvent.getAmount();
        AccountInfo accountInfoResult = accountInfoMapper.getByAccountNo(accountNo);
        BigDecimal accountBalance = accountInfoResult.getAccountBalance();
        if (accountBalance.compareTo(amount) < 0) {
            throw new RuntimeException("账户余额不够");
        }

        amount = accountBalance.subtract(amount);
        accountInfoMapper.updateAccountBalance(accountNo, amount, accountChangeEvent.getUpdateTime());

        //插入事务记录，用于幂等校验
        DeDuplicationRmbenas deDuplicationRmbenas = new DeDuplicationRmbenas();
        deDuplicationRmbenas.setTxNo(id);
        deDuplicationRmbenas.setCreateTime(new Date());
        deDuplicationRmbenasMapper.addTx(deDuplicationRmbenas);
    }

    @Override
    public AccountPay getById(String id) {
        AccountPay accountPay = rmbenPaysClient.getById(id);

        //更新账户金额
        if ("SUCCESS".equals(accountPay.getStatus())) {
            AccountChangeEvent accountChangeEvent = new AccountChangeEvent();
            accountChangeEvent.setAccountNo(accountPay.getAccountNo());
            accountChangeEvent.setAmount(accountPay.getPayAmount());
            accountChangeEvent.setTxNo(accountPay.getId());
            updateAccountBalance(accountChangeEvent);
        }
        return accountPay;
    }
}
