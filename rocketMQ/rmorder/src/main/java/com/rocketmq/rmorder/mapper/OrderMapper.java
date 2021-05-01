package com.rocketmq.rmorder.mapper;

import com.rocketmq.rmorder.entity.OrderInfo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderMapper {

    int addOrder(OrderInfo orderInfo) ;

    OrderInfo getByOrderId(String orderId);
}
