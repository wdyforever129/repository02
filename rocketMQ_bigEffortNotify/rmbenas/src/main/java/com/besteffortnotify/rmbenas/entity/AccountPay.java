package com.besteffortnotify.rmbenas.entity;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class AccountPay implements Serializable {

    private String id;
    private String accountNo;
    private BigDecimal payAmount;
    private String status;
    private Date createTime;
    private Date updateTime;
    private String createBy;
    private String updateBy;
}
