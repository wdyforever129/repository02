package com.rocketmq.rmdispatch.mapper;

import com.rocketmq.rmdispatch.entity.DeDuplicationRmdispatch;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DeDuplicationRmdispatchMapper {

    //insert into de_duplication values(#{txNo}, now())
    void addTx(DeDuplicationRmdispatch deDuplicationRmdispatch);

    //select count(1) from de_duplication where tx_no = #{txNo}
    int isExistTx(String txNo);
}
