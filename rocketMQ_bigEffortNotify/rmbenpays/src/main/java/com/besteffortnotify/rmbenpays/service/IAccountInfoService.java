package com.besteffortnotify.rmbenpays.service;

import com.besteffortnotify.rmbenpays.entity.AccountPay;

public interface IAccountInfoService {

    AccountPay insertAccountPay(AccountPay accountPay);

    AccountPay getById(String txNo);
}
