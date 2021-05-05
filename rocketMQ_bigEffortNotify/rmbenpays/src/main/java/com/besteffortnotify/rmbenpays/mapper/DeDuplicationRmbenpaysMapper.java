package com.besteffortnotify.rmbenpays.mapper;

import com.besteffortnotify.rmbenpays.entity.DeDuplicationRmbenpays;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DeDuplicationRmbenpaysMapper {

    //insert into de_duplication values(#{txNo}, now())
    void addTx(DeDuplicationRmbenpays deDuplicationRmbenas);

    //select count(1) from de_duplication where tx_no = #{txNo}
    int isExistTx(String txNo);
}
