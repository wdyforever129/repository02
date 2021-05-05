package com.besteffortnotify.rmbenas.remote;

import com.besteffortnotify.rmbenas.entity.AccountPay;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient(value = "rmbenpays-server", fallback = PayFallback.class)//fallback调用降级
public interface RmbenPaysClient {

    @RequestMapping(value = "/rmbenpays/pay/getById/{id}")
    AccountPay getById(@PathVariable String id);
}
