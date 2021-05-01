package com.evan.cm.service.impl;

import com.evan.cm.entity.GoodsInfo;
import com.evan.cm.mapper.GoodsInfoMapper;
import com.evan.cm.service.IGoodsInfoService;
import lombok.extern.slf4j.Slf4j;
import org.junit.platform.commons.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class GoodsInfoServiceImpl implements IGoodsInfoService {
    @Autowired
    private GoodsInfoMapper goodsInfoMapper;

    @Override
    public String addGoodsInfo(GoodsInfo goodsInfo) {
        if (goodsInfo == null) {
            log.info("请输入商品信息");
        } else if (StringUtils.isNotBlank(goodsInfo.getGoodsId())) {
            goodsInfoMapper.addGoodsInfo(goodsInfo);
        }
        return "添加信息成功";
    }

    @Override
    public GoodsInfo getByGoodsId(GoodsInfo goodsInfo) {
        return goodsInfoMapper.getByGoodsId(goodsInfo.getGoodsId());
    }
}
