package com.besteffortnotify.rmbenpays.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class DeDuplicationRmbenpays implements Serializable {

    private String txNo;

    private Date createTime;

}
