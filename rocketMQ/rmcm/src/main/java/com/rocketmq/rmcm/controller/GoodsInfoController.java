package com.rocketmq.rmcm.controller;

import com.rocketmq.rmcm.entity.GoodsInfo;
import com.rocketmq.rmcm.service.IGoodsInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GoodsInfoController {
    @Autowired
    private IGoodsInfoService goodsInfoServiceImpl;

    @RequestMapping(value = "/addGoodsInfo", method = RequestMethod.POST)
    public String addGoodsInfo(@RequestBody GoodsInfo goodsInfo){
        return goodsInfoServiceImpl.addGoodsInfo(goodsInfo);
    }

    @RequestMapping(value = "/getByGoodsId", method = RequestMethod.POST)
    public GoodsInfo getByGoodsId(@RequestBody GoodsInfo goodsInfo){
        return goodsInfoServiceImpl.getByGoodsId(goodsInfo);
    }
}
