package com.besteffortnotify.rmbenas.mapper;

import com.besteffortnotify.rmbenas.entity.DeDuplicationRmbenas;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DeDuplicationRmbenasMapper {

    //insert into de_duplication values(#{txNo}, now())
    void addTx(DeDuplicationRmbenas deDuplicationRmbenas);

    //select count(1) from de_duplication where tx_no = #{txNo}
    int isExistTx(String txNo);
}
