package com.besteffortnotify.rmbenpays.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class AccountInfo implements Serializable {

    private String id;
    private String accountName;
    private String accountNo;
    private String accountPassword;
    private String accountBalance;
    private String createTime;
    private String updateTime;
    private String createBy;
    private String updateBy;
}
