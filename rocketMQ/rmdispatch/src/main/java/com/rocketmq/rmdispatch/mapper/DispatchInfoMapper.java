package com.rocketmq.rmdispatch.mapper;

import com.rocketmq.rmdispatch.entity.DispatchInfo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DispatchInfoMapper {

    int addDispatchInfo(DispatchInfo dispatchInfo);
}
