package com.rocketmq.rmorder.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class OrderDispatchEvent implements Serializable {

    private String orderId;

    private String goodsId;

    private int sum;

    /**
     * 事务号，用于幂等性校验用
     */
    private String txNo;
}
