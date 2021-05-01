package com.rocketmq.rmorder.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class OrderDTO implements Serializable {
    private String goodsId;
    private int sum;
}
