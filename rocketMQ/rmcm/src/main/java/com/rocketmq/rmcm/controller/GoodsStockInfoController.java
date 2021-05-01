package com.rocketmq.rmcm.controller;

import com.rocketmq.rmcm.entity.GoodsStockInfo;
import com.rocketmq.rmcm.service.IGoodsStockInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GoodsStockInfoController {
    @Autowired
    private IGoodsStockInfoService goodsStockInfoServiceImpl;

    @RequestMapping(value = "/addGoodsStockInfo", method = RequestMethod.POST)
    public String addGoodsInfo(@RequestBody GoodsStockInfo GoodsStockInfo){
        return goodsStockInfoServiceImpl.addStockNum(GoodsStockInfo);
    }
}
