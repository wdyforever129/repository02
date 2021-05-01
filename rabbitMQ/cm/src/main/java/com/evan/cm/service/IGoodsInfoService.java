package com.evan.cm.service;

import com.evan.cm.entity.GoodsInfo;

public interface IGoodsInfoService {

    String addGoodsInfo(GoodsInfo goodsInfo);

    GoodsInfo getByGoodsId(GoodsInfo goodsInfo);
}
