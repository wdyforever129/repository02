package com.evan.cm.mapper;

import com.evan.cm.entity.GoodsStockInfo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface GoodsStockMapper {

    int addGoodsInfo(GoodsStockInfo goodsStockInfo);

    GoodsStockInfo getByGoodsId(String goodsId);

    int updateByGoodsId(GoodsStockInfo goodsStockInfo);
}
