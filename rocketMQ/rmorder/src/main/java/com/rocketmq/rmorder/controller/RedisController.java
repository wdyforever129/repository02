package com.rocketmq.rmorder.controller;

import com.rocketmq.rmorder.service.IRedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/redis")
public class RedisController {
    @Autowired
    private IRedisService redisServiceImpl;

    @RequestMapping("/deductStock")
    public String deductStock() throws InterruptedException {
        return redisServiceImpl.deductStock();
    }
}
