package com.besteffortnotify.rmbenas.controller;

import com.besteffortnotify.rmbenas.entity.AccountPay;
import com.besteffortnotify.rmbenas.service.IAccountInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rmbenas")
public class AccountInfoController {
    @Autowired
    private IAccountInfoService accountInfoServiceImpl;

    @RequestMapping("/updateAccountInfo")
    public AccountPay updateAccountById(String id){
        return accountInfoServiceImpl.getById(id);
    }
}
