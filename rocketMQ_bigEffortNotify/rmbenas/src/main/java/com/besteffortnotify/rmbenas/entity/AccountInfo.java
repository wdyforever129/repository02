package com.besteffortnotify.rmbenas.entity;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class AccountInfo implements Serializable {

    private String id;
    private String accountName;
    private String accountNo;
    private String accountPassword;
    private BigDecimal accountBalance;
    private String createTime;
    private String updateTime;
    private String createBy;
    private String updateBy;
}
