package com.rocketmq.rmdispatch.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class DeDuplicationRmdispatch implements Serializable {

    private String txNo;

    private Date createTime;

}
