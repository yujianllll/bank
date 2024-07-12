package com.example.trade_service.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.bkapi.feign.SolderClient;
import com.example.solder.dto.Result;
import com.example.solder.dto.OrderDetailDTO;
import com.example.solder.entity.Solder;
import com.example.trade_service.dto.MultiDelayMessage;
import com.example.trade_service.entity.Order;
import com.example.trade_service.mapper.OrderMapper;
import com.example.trade_service.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static com.example.trade_service.dto.MqConstants.DELAY_EXCHANGE;
import static com.example.trade_service.dto.MqConstants.DELAY_ORDER_ROUTING_KEY;

/**
* @author 蒋浩宇
* @description 针对表【order】的数据库操作Service实现
* @createDate 2024-07-01 11:37:17
*/
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService{
    @Resource
    SolderClient solderClient;
    @Resource
    RabbitTemplate rabbitTemplate;
    @Autowired
    OrderMapper orderMapper;
    @Autowired
    RedissonClient redissonClient;

    public OrderServiceImpl(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public com.example.school.dto.Result findtime(Order id) {
        LocalDateTime time =LocalDateTime.now();
        id.setCreatTime(Timestamp.valueOf(time));
        return null;
    }
    private final ObjectMapper objectMapper;
    @Override
    public String createOrder(OrderDetailDTO detailDTO, String user) {
        //通过id得到买的东西，以及个人信息
        Result result = solderClient.findbyid(detailDTO.getItemId());
        System.out.println(result.getData().toString());
        Solder solder = objectMapper.convertValue(result.getData(), Solder.class);
        Order order = new Order();
        order.setCreatTime(Timestamp.valueOf(LocalDateTime.now()));
        String id = user + detailDTO.getItemId().toString() + LocalDateTime.now().toString();
        String newid = id.replaceAll("[^\\d]", "");
        order.setId(newid);
        order.setStatus(1);
        order.setUserId(Long.parseLong(user));
        order.setPrice(solder.getPrice());
        order.setSolderId(detailDTO.getItemId());
        save(order);
        // 3.扣减库存
        try {
            solderClient.deductStock(detailDTO);
        } catch (Exception e) {
            throw new RuntimeException("库存不足！");
        }
        isokZhangdan(order);
        return newid;
    }
    private void isokZhangdan(Order order)//检查账单是否已经支付
    {
        MultiDelayMessage<String> msg = MultiDelayMessage.of(order.getId(), 10000L, 10000L, 10000L, 15000L, 15000L, 30000L, 30000L);
        //第一次发送延时消息
        try {
            rabbitTemplate.convertAndSend(DELAY_EXCHANGE, DELAY_ORDER_ROUTING_KEY,
                    msg, new MessagePostProcessor() {
                        @Override
                        public Message postProcessMessage(Message message) throws AmqpException {
                            message.getMessageProperties().setDelay(msg.removeNextDelay().intValue());
                            return message;
                        }
                    });
        } catch (AmqpException e) {
            e.printStackTrace();
            log.error("延迟发送失败");
        }
    }
    @Override
    @Transactional
    public void cancelOrder(String orderId) {
        //取消支付
        Order order = new Order();
        order.setId(orderId);
        order.setStatus(5);
        updateById(order);
        OrderDetailDTO detailDTO = new OrderDetailDTO();
        Order order1 = query().eq("id",order.getId()).one();
        detailDTO.setItemId(order1.getSolderId());
        detailDTO.setNum(-1);
        //恢复库存
        solderClient.deductStock(detailDTO);
    }
    public List<Order> getOrdersWithoutReview(LocalDateTime cutoffDate) {
        return orderMapper.selectOrdersWithoutReview(cutoffDate);
    }

    @Override
    public String createOrders(OrderDetailDTO detailDTO, String user) {

        // 创建锁对象
        RLock redisLock = redissonClient.getLock("lock:order:" + user);
        // 尝试获取锁
        boolean isLock = redisLock.tryLock();
        // 判断
        if (!isLock) {
            // 获取锁失败，直接返回失败或者重试
            log.error("不允许重复下单！");
            return null;
        }
        try {
            LocalDateTime startTime = LocalDateTime.now().minus(1, ChronoUnit.DAYS);
            int count = query().eq("user_id",Long.parseLong(user)).eq("solder_id",detailDTO.getItemId())
                    .ge("creatTime",startTime).count();
            if (count > 0) {
                // 用户已经购买过了
                log.error("不允许重复下单！");
                return null;
            }
            //通过id得到买的东西，以及个人信息
            saveOrder(detailDTO,user);
            // 3.扣减库存
            try {
                solderClient.deductStock(detailDTO);
            } catch (Exception e) {
                throw new RuntimeException("库存不足！");
            }
        } finally {
           redisLock.unlock();
        }
        return null;
    }
    private void saveOrder(OrderDetailDTO detailDTO,String user)
    {
        //通过id得到买的东西，以及个人信息
        Result result = solderClient.findbyid(detailDTO.getItemId());
        System.out.println(result.getData().toString());
        Solder solder = objectMapper.convertValue(result.getData(), Solder.class);
        Order order = new Order();
        order.setCreatTime(Timestamp.valueOf(LocalDateTime.now()));
        String id = user + detailDTO.getItemId().toString() + LocalDateTime.now().toString();
        String newid = id.replaceAll("[^\\d]", "");
        order.setId(newid);
        order.setStatus(1);
        order.setUserId(Long.parseLong(user));
        order.setPrice(solder.getPrice());
        order.setSolderId(detailDTO.getItemId());
        save(order);
    }
}




