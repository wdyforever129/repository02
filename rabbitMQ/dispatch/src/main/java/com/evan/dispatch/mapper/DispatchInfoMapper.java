package com.evan.dispatch.mapper;

import com.evan.dispatch.entity.DispatchInfo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DispatchInfoMapper {

    int addDispatchInfo(DispatchInfo dispatchInfo);
}
