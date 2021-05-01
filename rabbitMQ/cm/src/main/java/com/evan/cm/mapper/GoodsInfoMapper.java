package com.evan.cm.mapper;

import com.evan.cm.entity.GoodsInfo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface GoodsInfoMapper {

    int addGoodsInfo(GoodsInfo goodsInfo);

    GoodsInfo getByGoodsId(String goodsId);
}
