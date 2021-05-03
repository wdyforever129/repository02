package com.rocketmq.rmdispatch.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class DispatchInfo implements Serializable {
    private int id;
    private String orderId;
    private String dispatchRoute;
    private Long takeoutUserId;
    private Date createTime;
}
