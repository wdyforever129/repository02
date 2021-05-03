package com.rocketmq.rmorder.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class DeDuplicationRmorder implements Serializable {

    private String txNo;

    private Date createTime;

}
