package com.example.trade_service.listener;

import com.example.trade_service.entity.Order;
import com.example.trade_service.entity.PayJuge;
import com.example.trade_service.service.OrderService;
import com.example.trade_service.service.PayJugeService;
import com.example.trade_service.util.MqContent;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class payandp {

    @Autowired
    PayJugeService payJugeService;
    @Autowired
    OrderService orderService;
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = MqContent.MQ_PYDE_Queue, durable = "true"),
            exchange = @Exchange(value = MqContent.MQ_PAYDE, delayed = "true", type = ExchangeTypes.TOPIC),
            key = MqContent.DELAY_ORDER_ROUTING_KEY
    ))
    public void listener(String OrderId)//这里还有更好的方式进行处理，设置分批处理事务
    {
        Order order = orderService.query().eq("id",OrderId).one();
        PayJuge payJuge = payJugeService.query().eq("pay_id",OrderId).one();
        if(payJuge!=null)
        {
            System.out.println("已对订单进行评价");
            throw new RuntimeException("已对订单进行评价");
        }
        PayJuge payJuge1 = new PayJuge();
        payJuge1.setJuge(5);
        payJuge1.setPayId(order.getId());
        payJuge1.setUserId(order.getUserId());
        payJuge1.setSolderId(order.getSolderId());
        payJuge1.setContent("系统默认好评");
        payJuge1.setCreateTime(Timestamp.valueOf(LocalDateTime.now()));
        payJugeService.save(payJuge1);
    }
}
