package com.rocketmq.rmorder.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.rocketmq.rmorder.dto.OrderDTO;
import com.rocketmq.rmorder.entity.DeDuplicationRmorder;
import com.rocketmq.rmorder.entity.GoodsInfo;
import com.rocketmq.rmorder.entity.OrderInfo;
import com.rocketmq.rmorder.mapper.DeDuplicationRmorderMapper;
import com.rocketmq.rmorder.mapper.OrderMapper;
import com.rocketmq.rmorder.model.OrderDispatchEvent;
import com.rocketmq.rmorder.remote.DistributedCm;
import com.rocketmq.rmorder.service.IOrderTaskService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Service
public class OrderTaskServiceImpl implements IOrderTaskService {
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private RocketMQTemplate rocketMQTemplate;
    @Autowired
    private DistributedCm distributedCm;
    @Autowired
    private Redisson redisson;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private DeDuplicationRmorderMapper deDuplicationRmorderMapper;

    @Override
    public String order(OrderDTO orderDTO) {
        GoodsInfo goodsInfo;
        try {
            GoodsInfo record = new GoodsInfo();
            record.setGoodsId(orderDTO.getGoodsId());
            goodsInfo = distributedCm.getByGoodsId(record);//正常项目是从前端直接带过来
            if (goodsInfo == null) {
                throw new RuntimeException("抛错：商品信息不存在");
            }
        } catch (Exception e) {
            throw new RuntimeException("抛错：调用接口失败");
        }

        String goodsId = goodsInfo.getGoodsId();
        RLock lock = redisson.getLock(goodsId + goodsInfo.getName());
        try {
            lock.lock();

            String stockSumResult = stringRedisTemplate.opsForValue().get(orderDTO.getGoodsId());
            if (StringUtils.isBlank(stockSumResult)) {
                throw new RuntimeException("抛错：没有库存");
            }

            long stockSum = Long.parseLong(stockSumResult);
            int sum = orderDTO.getSum();
            if (stockSum < sum) {
                throw new RuntimeException("抛错：库存数量不够");
            }

            OrderDispatchEvent orderDispatchEvent = new OrderDispatchEvent();
            String orderId = UUID.randomUUID().toString();
            orderDispatchEvent.setOrderId(orderId);
            orderDispatchEvent.setGoodsId(orderDTO.getGoodsId());
            orderDispatchEvent.setSum(orderDTO.getSum());
            orderDispatchEvent.setTxNo("TX" + orderId);
            send(orderDispatchEvent);

            long newStockSum = stockSum - sum;
            stringRedisTemplate.opsForValue().set(goodsId, newStockSum + "");
            return "下单成功";
        } finally {
            //执行完成后释放锁
            lock.unlock();
        }
    }

    //2、使用消息中间件将参数存在派单队列中
    private void send(OrderDispatchEvent orderDispatchEvent) {

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("orderDispatchEvent", orderDispatchEvent);
        String body = jsonObject.toJSONString();
        //生成msg消息
        Message<String> msg = MessageBuilder.withPayload(body).build();
        //发送一条事务消息
        //String destination:topic, Message<?> message:消息内容, Object arg：参数
        rocketMQTemplate.sendMessageInTransaction("producer_group_rmorder","orderRoutingKey", msg, null);

        /*Message message = MessageBuilder.withBody(msg.getBytes()).setContentType(MessageProperties.CONTENT_TYPE_JSON).setContentEncoding("utf-8").setMessageId(orderId).build();
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setConfirmCallback(this);
        CorrelationData correlationData = new CorrelationData(orderId);//构建回调返回的数据（消息Id）
        //将消息发送到派单队列交换机order_exchange_name上，并根据key=orderRoutingKey路由到派单队列ORDER_DIC_QUEUE中
        rabbitTemplate.convertAndSend("order_exchange_name", "orderRoutingKey", message, correlationData);*/
    }

    @Override
    @Transactional
    public void addOrderAndDispatch(OrderDispatchEvent orderDispatchEvent) {
        GoodsInfo goodsInfo;
        try {
            GoodsInfo record = new GoodsInfo();
            record.setGoodsId(orderDispatchEvent.getGoodsId());
            goodsInfo = distributedCm.getByGoodsId(record);//正常项目是从前端直接带过来
            if (goodsInfo == null) {
                throw new RuntimeException("抛错：商品信息不存在");
            }
        } catch (Exception e) {
            throw new RuntimeException("抛错：调用接口失败");
        }

        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setName(goodsInfo.getName());
        orderInfo.setOrderMoney(goodsInfo.getPrice().multiply(new BigDecimal(orderDispatchEvent.getSum())));//价格是300元
        orderInfo.setOrderState(0);
        Long commodityId = 30L;
        orderInfo.setCommodityId(commodityId);//商品Id
        String orderId = UUID.randomUUID().toString();
        orderInfo.setOrderId(orderId);
        orderInfo.setCreateTime(new Date());

        //幂等判断
        if (deDuplicationRmorderMapper.isExistTx(orderDispatchEvent.getTxNo()) > 0) {
            return ;
        }

        //1、先下单，创建订单（往订单数据库中插入一条数据）
        int orderResult = orderMapper.addOrder(orderInfo);
        //添加事务日志
        DeDuplicationRmorder deDuplicationRmorder = new DeDuplicationRmorder();
        deDuplicationRmorder.setTxNo("TX"+orderDispatchEvent.getTxNo());
        deDuplicationRmorder.setCreateTime(new Date());
        deDuplicationRmorderMapper.addTx(deDuplicationRmorder);
        log.info("orderResult:" + orderResult);
        if (orderResult <= 0) {
            throw new RuntimeException("下单失败！");
        }
        //int i = 1/0;//如果在这个位置报错该怎么保证redis中数据回滚的问题
    }

    //生成消息确认机制 生产者往服务器端发送消息的时候，采用应答机制
    /*@Override
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
    }*/


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
