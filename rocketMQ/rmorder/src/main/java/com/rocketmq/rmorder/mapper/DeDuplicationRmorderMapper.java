package com.rocketmq.rmorder.mapper;

import com.rocketmq.rmorder.entity.DeDuplicationRmorder;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DeDuplicationRmorderMapper {

    //insert into de_duplication values(#{txNo}, now())
    void addTx(DeDuplicationRmorder deDuplicationRmorder);

    //select count(1) from de_duplication where tx_no = #{txNo}
    int isExistTx(String txNo);
}
