package com.besteffortnotify.rmbenpays.controller;

import com.besteffortnotify.rmbenpays.entity.AccountPay;
import com.besteffortnotify.rmbenpays.service.IAccountInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.UUID;

@RestController
@RequestMapping("/pay")
public class AccountPayController {
    @Autowired
    private IAccountInfoService accountInfoServiceImpl;

    @RequestMapping(value = "/add")
    public AccountPay pay(@RequestBody AccountPay accountPay) {
        String txNo = UUID.randomUUID().toString();
        accountPay.setId(txNo);
        accountPay.setCreateTime(new Date());
        accountPay.setUpdateTime(new Date());
        return accountInfoServiceImpl.insertAccountPay(accountPay);
    }

    @RequestMapping(value = "/getById/{id}", method = RequestMethod.GET)
    public AccountPay getById(@PathVariable String id) {
        return accountInfoServiceImpl.getById(id);
    }
}
