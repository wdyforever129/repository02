package com.distributed.order.service.impl;

import com.distributed.order.service.IRedisService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

/**
 * 分布式锁实现
 * 两个问题未解决：
 * 1、redis主从锁失效的问题？主服务器宕机时，同步的性的问题，
 * 1.1、zookeeper可以彻底解决，但zookeeper能彻底解决但性能可能收到影响
 * 1.2、rediLock
 */
@Slf4j
@Service
public class RedisServiceImpl implements IRedisService {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private Redisson redisson;

    public String deductStock() throws InterruptedException {

        /*synchronized (this) {*///jvm级别的锁用synchronzed是不生效的

        String lockKey = "lockKey";
        /*String clientId = UUID.randomUUID().toString();*/

        RLock lock = redisson.getLock(lockKey);//获取锁对象

        try {
            //redis实现分布式锁，是单线程的，所以同一个时间只能有一个线程拿到锁，获取锁的原理就是用setIfAbsent()方法在redis中只能设置一个值，设置过了是不能在设置值的。
            /*Boolean result = stringRedisTemplate.opsForValue().setIfAbsent(lockKey, "evan");//设置成功表示拿到锁
            stringRedisTemplate.expire(lockKey, 10, TimeUnit.SECONDS);*///给锁设置超时时间，如果10秒还没释放锁redis会自动释放锁
            /*Boolean result = stringRedisTemplate.opsForValue().setIfAbsent(lockKey, clientId, 30, TimeUnit.SECONDS);
            if (!result) {
                return "有锁存在，请等待其他服务执行完成在调用";
            }*/

            lock.lock();//进行加锁，默认锁失效时间是30s，每过10s(1/3的时间为准)看锁是否还存在，如果存在则重新将失效时间设置为30s
            int stock = Integer.parseInt(stringRedisTemplate.opsForValue().get("stock"));
            if (stock > 0) {
                stock -= 1;
                stringRedisTemplate.opsForValue().set("stock", stock + "");
                log.info("扣减成功，剩余库存：" + stock);
            } else {
                log.info("扣减失败，库存不足");
            }
            /*}*/
        } finally {
            //执行完成后释放锁
            lock.unlock();
            /*if (clientId.equals(stringRedisTemplate.opsForValue().get(lockKey))){
                stringRedisTemplate.delete(lockKey);
            }*/
        }
        return "end";
    }
}
