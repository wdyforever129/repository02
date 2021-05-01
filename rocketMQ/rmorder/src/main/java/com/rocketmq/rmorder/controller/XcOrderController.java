package com.rocketmq.rmorder.controller;

import com.rocketmq.rmorder.dto.OrderDTO;
import com.rocketmq.rmorder.service.IOrderTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class XcOrderController {
    @Autowired
    private IOrderTaskService orderTaskService;

    @RequestMapping(value = "/addOrder",method = RequestMethod.POST)
    public String addOrder(@RequestBody OrderDTO orderDTO){
        return orderTaskService.addOrderAndDispatch(orderDTO);
    }
}
