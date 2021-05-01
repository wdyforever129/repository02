package com.distributed.order.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.distributed.order.dto.OrderDTO;
import com.distributed.order.entity.GoodsInfo;
import com.distributed.order.entity.OrderInfo;
import com.distributed.order.mapper.OrderMapper;
import com.distributed.order.remote.DistributedCm;
import com.distributed.order.service.IOrderTaskService;
import lombok.extern.slf4j.Slf4j;
import org.junit.platform.commons.util.StringUtils;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Service
public class OrderTaskServiceImpl implements IOrderTaskService, RabbitTemplate.ConfirmCallback {
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private DistributedCm distributedCm;
    @Autowired
    private Redisson redisson;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    @Transactional
    public String addOrderAndDispatch(OrderDTO orderDTO) {

        GoodsInfo goodsInfo = null;
        try {
            GoodsInfo record = new GoodsInfo();
            record.setGoodsId(orderDTO.getGoodsId());
            goodsInfo = distributedCm.getByGoodsId(record);//正常项目是从前端直接带过来
        } catch (Exception e) {
            log.info("抛错：调用接口失败");
        }

        if (goodsInfo == null) {
            log.info("抛错：商品信息不存在");
        } else {
            String goodsId = goodsInfo.getGoodsId();
            RLock lock = redisson.getLock(goodsId + goodsInfo.getName());

            try {
                lock.lock();
                String stockSumResult = stringRedisTemplate.opsForValue().get(goodsId);
                if (StringUtils.isBlank(stockSumResult)) {
                    log.info("抛错：没有库存");
                } else {
                    long stockSum = Long.parseLong(stockSumResult);
                    int sum = orderDTO.getSum();
                    if (stockSum < sum) {
                        log.info("抛错：库存数量不够");
                    } else {
                        OrderInfo orderInfo = new OrderInfo();
                        orderInfo.setName(goodsInfo.getName());
                        orderInfo.setCreateTime(new Date().toString());
                        orderInfo.setOrderMoney(goodsInfo.getPrice().multiply(new BigDecimal(sum)));//价格是300元
                        orderInfo.setOrderState(0);
                        Long commodityId = 30L;
                        orderInfo.setCommodityId(commodityId);//商品Id
                        String orderId = UUID.randomUUID().toString();
                        orderInfo.setOrderId(orderId);

                        //1、先下单，创建订单（往订单数据库中插入一条数据）
                        int orderResult = orderMapper.addOrder(orderInfo);
                        log.info("orderResult:" + orderResult);
                        if (orderResult <= 0) {
                            return "下单失败！";
                        }

                        //2、使用消息中间件将参数存在派单队列中
                        send(orderId);

                        long newStockSum = stockSum - sum;
                        stringRedisTemplate.opsForValue().set(goodsId, newStockSum + "");
                        //int i = 1/0;//如果在这个位置报错该怎么保证redis中数据回滚的问题
                    }
                }
            } finally {
                //执行完成后释放锁
                lock.unlock();
            }
        }
        return "下单成功";
    }

    private void send(String orderId) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("orderId", orderId);
        String msg = jsonObject.toJSONString();
        //封装消息
        Message message = MessageBuilder.withBody(msg.getBytes()).setContentType(MessageProperties.CONTENT_TYPE_JSON).setContentEncoding("utf-8").setMessageId(orderId).build();
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setConfirmCallback(this);
        CorrelationData correlationData = new CorrelationData(orderId);//构建回调返回的数据（消息Id）
        //将消息发送到派单队列交换机order_exchange_name上，并根据key=orderRoutingKey路由到派单队列ORDER_DIC_QUEUE中
        rabbitTemplate.convertAndSend("order_exchange_name", "orderRoutingKey", message, correlationData);
    }

    //生成消息确认机制 生产者往服务器端发送消息的时候，采用应答机制
    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        String orderId = correlationData.getId();
        log.info("消息id:" + orderId);
        if (ack) {
            log.info("消息发送确认成功");
        } else {
            //重试机制
            send(orderId);
            log.info("消息发送确认失败：" + cause);
        }
    }


    /*@Override
    public List<XcOrderTask> listByUpdateTimeBefore(Date newDate, Date beforeDate) {
        XcOrderTask orderTask = new XcOrderTask();
        orderTask.setUpdateTime(beforeDate);
        List<XcOrderTask> orderTasks = xcOrderTaskMapper.getByUpdateTimeBefore(newDate, beforeDate);

        return orderTasks;
    }

    *//**
     * 乐观锁
     *//*
    @Override
    public int updateById(String id, int version) {
        return xcOrderTaskMapper.updateByIdAndVersion(id, version);
    }

    *//**
     * 发送消息
     *//*
    @Override
    public void send(XcOrderTask xcOrderTask, String exchange, String routingKey) {
        XcOrderTask orderTask = xcOrderTaskMapper.getById(xcOrderTask.getId());
        if (orderTask != null) {
            CorrelationData correlationData = new CorrelationData(orderTask.getId());//幂等性解决消息的重复发送的问题
            rabbitTemplate.convertAndSend(exchange, routingKey, JSONObject.toJSON(xcOrderTask), correlationData);//路由模式
            orderTask.setUpdateTime(new Date());
            xcOrderTaskMapper.updateById(orderTask);
        }
    }

    @Override
    public void finishOrderTaskInfo(String id) {
        XcOrderTask xcOrderTask = xcOrderTaskMapper.getById(id);
        if (xcOrderTask != null) {
            XcOrderTaskHist xcOrderTaskHist = new XcOrderTaskHist();
            BeanUtils.copyProperties(xcOrderTask, xcOrderTaskHist);
            xcOrderTaskHistMapper.insertOne(xcOrderTaskHist);
            xcOrderTaskMapper.deleteById(xcOrderTask);
        }
    }*/
}
