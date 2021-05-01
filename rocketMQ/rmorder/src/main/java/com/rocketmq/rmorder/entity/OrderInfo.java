package com.rocketmq.rmorder.entity;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class OrderInfo implements Serializable {
    private int id;
    private String name;
    private String createTime;
    private int orderState;
    private Long commodityId;
    private String orderId;
    private BigDecimal orderMoney;
}
