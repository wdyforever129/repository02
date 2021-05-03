package com.rocketmq.rmorder.remote;

import com.rocketmq.rmorder.entity.GoodsInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient("rmcm-server")
@RequestMapping("/rmcm")
public interface DistributedCm {

    @RequestMapping(value = "/getByGoodsId", method = RequestMethod.POST)
    GoodsInfo getByGoodsId(GoodsInfo record);
}
