package com.rocketmq.rmdispatch.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.rocketmq.rmdispatch.entity.DeDuplicationRmdispatch;
import com.rocketmq.rmdispatch.entity.DispatchInfo;
import com.rocketmq.rmdispatch.mapper.DeDuplicationRmdispatchMapper;
import com.rocketmq.rmdispatch.mapper.DispatchInfoMapper;
import com.rocketmq.rmdispatch.model.OrderDispatchEvent;
import com.rocketmq.rmdispatch.service.IDispatchInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Slf4j
@Service
public class DispatchInfoServiceImpl implements IDispatchInfoService {
    @Autowired
    private DispatchInfoMapper dispatchInfoMapper;
    @Autowired
    private DeDuplicationRmdispatchMapper deDuplicationRmdispatchMapper;

    @Override
    @Transactional
    public void addDispatchInfo(OrderDispatchEvent orderDispatchEvent) {
        log.info("开始增加新的派单：{}", JSONObject.toJSONString(orderDispatchEvent));

        //幂等性校验
        int existTx = deDuplicationRmdispatchMapper.isExistTx(orderDispatchEvent.getTxNo());
        if (existTx > 0) {
            return;
        }
        //添加派单
        DispatchInfo dispatchInfo = new DispatchInfo();
        dispatchInfo.setOrderId(orderDispatchEvent.getOrderId());
        dispatchInfo.setTakeoutUserId(12L);
        dispatchInfo.setDispatchRoute("40,40");
        dispatchInfoMapper.addDispatchInfo(dispatchInfo);

        //添加事务记录，用于幂等校验
        DeDuplicationRmdispatch deDuplicationRmdispatch = new DeDuplicationRmdispatch();
        deDuplicationRmdispatch.setTxNo("TX"+orderDispatchEvent.getTxNo());
        deDuplicationRmdispatch.setCreateTime(new Date());
        deDuplicationRmdispatchMapper.addTx(deDuplicationRmdispatch);
    }
}
