package com.besteffortnotify.rmbenpays.mapper;

import com.besteffortnotify.rmbenpays.entity.AccountPay;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AccountPayMapper {

    //
    int insert(AccountPay accountPay);

    //select * from account_pay where id = #{txNo}
    AccountPay getById(@Param("id") String id);
}
