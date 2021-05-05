package com.besteffortnotify.rmbenas.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class DeDuplicationRmbenas implements Serializable {

    private String txNo;

    private Date createTime;

}
