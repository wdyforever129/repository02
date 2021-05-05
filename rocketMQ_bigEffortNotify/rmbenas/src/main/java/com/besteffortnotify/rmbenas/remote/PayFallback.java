package com.besteffortnotify.rmbenas.remote;

import com.besteffortnotify.rmbenas.entity.AccountPay;

/**
 * Feign调用降级
 */
public class PayFallback implements RmbenPaysClient{

    @Override
    public AccountPay getById(String id) {
        AccountPay accountPay = new AccountPay();
        accountPay.setStatus("fail");
        return accountPay;
    }
}
