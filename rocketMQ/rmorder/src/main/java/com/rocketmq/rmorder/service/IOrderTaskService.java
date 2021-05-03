package com.rocketmq.rmorder.service;

import com.rocketmq.rmorder.dto.OrderDTO;
import com.rocketmq.rmorder.model.OrderDispatchEvent;

public interface IOrderTaskService {

    //向MQ发送消息
    String order(OrderDTO orderDTO);

    //更新本地事务
    void addOrderAndDispatch(OrderDispatchEvent orderDTO);

    /*List<XcOrderTask> listByUpdateTimeBefore(Date newDate, Date beforeDate);

    int updateById(String id, int version);

    void send(XcOrderTask xcOrderTask, String exchange, String routingKey);

    void finishOrderTaskInfo(String id);*/
}
