package com.besteffortnotify.rmbenas.mapper;

import com.besteffortnotify.rmbenas.entity.AccountInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.Date;

@Mapper
public interface AccountInfoMapper {

    int updateAccountBalance(@Param("accountNo") String accountNo, @Param("amount")BigDecimal amount, @Param("updateTime")Date updateTime);

    AccountInfo getByAccountNo(@Param("accountNo") String accountNo);
}
