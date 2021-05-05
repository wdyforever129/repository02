package com.besteffortnotify.rmbenas.service;

import com.besteffortnotify.rmbenas.entity.AccountPay;
import com.besteffortnotify.rmbenas.model.AccountChangeEvent;

public interface IAccountInfoService {

    void updateAccountBalance(AccountChangeEvent accountChangeEvent);

    AccountPay getById(String id);
}
