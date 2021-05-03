package com.rocketmq.rmdispatch.service;

import com.rocketmq.rmdispatch.model.OrderDispatchEvent;

public interface IDispatchInfoService {

    void addDispatchInfo(OrderDispatchEvent orderDispatchEvent);
}
