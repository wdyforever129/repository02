package com.distributed.order.entity;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class GoodsInfo {
    private String goodsId;
    private String name;
    private BigDecimal price;
    private String unit;
    private String description;
}
