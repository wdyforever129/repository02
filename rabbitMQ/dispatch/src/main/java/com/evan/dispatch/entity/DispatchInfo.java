package com.evan.dispatch.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class DispatchInfo implements Serializable {
    private int id;
    private String orderId;
    private String dispatchRoute;
    private Long takeoutUserId;
}
