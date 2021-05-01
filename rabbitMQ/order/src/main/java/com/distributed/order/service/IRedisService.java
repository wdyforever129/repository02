package com.distributed.order.service;

public interface IRedisService {

    String deductStock() throws InterruptedException;
}
