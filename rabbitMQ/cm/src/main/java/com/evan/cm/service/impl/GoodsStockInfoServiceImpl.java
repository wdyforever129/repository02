package com.evan.cm.service.impl;

import com.evan.cm.entity.GoodsInfo;
import com.evan.cm.entity.GoodsStockInfo;
import com.evan.cm.mapper.GoodsInfoMapper;
import com.evan.cm.mapper.GoodsStockMapper;
import com.evan.cm.service.IGoodsStockInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class GoodsStockInfoServiceImpl implements IGoodsStockInfoService {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private GoodsStockMapper goodsStockMapper;
    @Autowired
    private GoodsInfoMapper goodsInfoMapper;

    /**
     * 设置库存
     */
    @Override
    public String addStockNum(GoodsStockInfo goodsStockInfo) {
        String goodsId = goodsStockInfo.getGoodsId();
        GoodsInfo goodsInfo = goodsInfoMapper.getByGoodsId(goodsId);
        if (goodsInfo == null) {
            log.info("商品信息不存在");
        } else {
            GoodsStockInfo goodsStockInfoResult = goodsStockMapper.getByGoodsId(goodsId);
            if (goodsStockInfoResult == null) {
                goodsStockMapper.addGoodsInfo(goodsStockInfo);
            } else if (goodsStockInfoResult.getSum() != 0) {
                Long sum = goodsStockInfo.getSum() + goodsStockInfoResult.getSum();
                goodsStockInfo.setSum(sum);
                goodsStockMapper.updateByGoodsId(goodsStockInfo);
            }
            stringRedisTemplate.opsForValue().set(goodsId, goodsStockInfo.getSum() + "");
        }
        return "库存添加成功";
    }
}
