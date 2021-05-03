package com.rocketmq.rmorder.entity;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class OrderInfo implements Serializable {
    private int id;
    private String name;
    private int orderState;
    private Long commodityId;
    private String orderId;
    private BigDecimal orderMoney;
    private Date createTime;
}
