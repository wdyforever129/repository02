package com.rocketmq.rmcm.mapper;

import com.rocketmq.rmcm.entity.GoodsStockInfo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface GoodsStockMapper {

    int addGoodsInfo(GoodsStockInfo goodsStockInfo);

    GoodsStockInfo getByGoodsId(String goodsId);

    int updateByGoodsId(GoodsStockInfo goodsStockInfo);
}
