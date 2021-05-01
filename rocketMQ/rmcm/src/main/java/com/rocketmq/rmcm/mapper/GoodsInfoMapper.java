package com.rocketmq.rmcm.mapper;

import com.rocketmq.rmcm.entity.GoodsInfo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface GoodsInfoMapper {

    int addGoodsInfo(GoodsInfo goodsInfo);

    GoodsInfo getByGoodsId(String goodsId);
}
