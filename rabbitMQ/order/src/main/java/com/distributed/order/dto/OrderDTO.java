package com.distributed.order.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class OrderDTO implements Serializable {
    private String goodsId;
    private int sum;
}
