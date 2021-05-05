package com.besteffortnotify.rmbenas.model;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class AccountChangeEvent implements Serializable {

    private String txNo;
    private String accountNo;
    private BigDecimal amount;
    private String status;
    private Date updateTime;
}
