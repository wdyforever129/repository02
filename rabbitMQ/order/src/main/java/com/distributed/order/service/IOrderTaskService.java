package com.distributed.order.service;

import com.distributed.order.dto.OrderDTO;

public interface IOrderTaskService {

    String addOrderAndDispatch(OrderDTO orderDTO);

    /*List<XcOrderTask> listByUpdateTimeBefore(Date newDate, Date beforeDate);

    int updateById(String id, int version);

    void send(XcOrderTask xcOrderTask, String exchange, String routingKey);

    void finishOrderTaskInfo(String id);*/
}
