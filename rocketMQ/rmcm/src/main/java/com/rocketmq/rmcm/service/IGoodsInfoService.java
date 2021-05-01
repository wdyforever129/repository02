package com.rocketmq.rmcm.service;

import com.rocketmq.rmcm.entity.GoodsInfo;

public interface IGoodsInfoService {

    String addGoodsInfo(GoodsInfo goodsInfo);

    GoodsInfo getByGoodsId(GoodsInfo goodsInfo);
}
