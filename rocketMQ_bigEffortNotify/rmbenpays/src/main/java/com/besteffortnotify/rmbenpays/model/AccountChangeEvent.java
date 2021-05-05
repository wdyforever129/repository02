package com.besteffortnotify.rmbenpays.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class AccountChangeEvent implements Serializable {

    private String id;
    private String accountNo;
    private String payAmount;
    private String status;
}
