package com.rocketmq.rmorder.service;

public interface IRedisService {

    String deductStock() throws InterruptedException;
}
