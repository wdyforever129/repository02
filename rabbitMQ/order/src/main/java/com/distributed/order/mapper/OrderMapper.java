package com.distributed.order.mapper;

import com.distributed.order.entity.OrderInfo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderMapper {

    int addOrder(OrderInfo orderInfo) ;

    OrderInfo getByOrderId(String orderId);
}
