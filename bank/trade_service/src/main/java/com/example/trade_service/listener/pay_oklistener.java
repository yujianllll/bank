package com.example.trade_service.listener;

import cn.hutool.json.JSONUtil;
import com.example.bkapi.feign.SolderClient;
import com.example.school.dto.Result;
import com.example.solder.entity.Solder;
import com.example.trade_service.entity.Order;
import com.example.trade_service.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import static com.example.solder.util.shopcontants.CACHE_HOT_SHOP_KEY;

@Component
@RequiredArgsConstructor
public class pay_oklistener {
    private final OrderService orderService;
    @Resource
    SolderClient solderClient;
    @Autowired
    StringRedisTemplate stringRedisTemplate;

    private final ObjectMapper objectMapper;
    @Resource
    private Executor taskExecutor;
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
//        executor.setCorePoolSize(10);
//        executor.setMaxPoolSize(20);
//        executor.setQueueCapacity(100);
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "mark.order.pay.queue", durable = "true"),
            exchange = @Exchange(name = "pay.topic", type = ExchangeTypes.TOPIC),
            key = "pay.success"
    ))
    public void listenOrderPay(String orderId) {
        // update order set status = 2 where id = ? AND status = 1
//        orderService.lambdaUpdate()
//                .set(Order::getStatus, 2)
//                .set(Order::getPayTime, LocalDateTime.now())
//                .eq(Order::getId, orderId)
//                .eq(Order::getStatus, 1)
//                .update();
        updatepay(orderId,2);
        System.out.println("更新成功");
        //如果更新
        CompletableFuture.runAsync(() -> {
            try {
                Order order = orderService.query().eq("id", orderId).one();
                Result result = solderClient.findbyid(order.getSolderId());
                System.out.println(result.getData().toString());
                Solder solder = objectMapper.convertValue(result.getData(), Solder.class);
                String solderjs = JSONUtil.toJsonStr(solder);
                stringRedisTemplate.opsForZSet().remove(CACHE_HOT_SHOP_KEY,solderjs);
                double score = Double.parseDouble(solder.getIsAd() * 500 + solder.getSold());
                stringRedisTemplate.opsForZSet().add(CACHE_HOT_SHOP_KEY, solderjs, score);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, taskExecutor);
    }
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "mark.order.pay.queuecancle", durable = "true"),
            exchange = @Exchange(name = "pay.topic", type = ExchangeTypes.TOPIC),
            key = "pay.cancle"
    ))
    public void listenOrderPay2(String orderId)
    {
        updatepay(orderId,5);
        System.out.println("更新成功");
    }
    private void updatepay(String orderid,long status)
    {
        // update order set status = 2 where id = ? AND status = 1
        orderService.lambdaUpdate()
                .set(Order::getStatus, status)
                .set(Order::getPayTime, LocalDateTime.now())
                .eq(Order::getId, orderid)
                .eq(Order::getStatus, 1)
                .update();
        System.out.println("更新成功");
    }
}
