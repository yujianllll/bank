package com.example.trade_service.conpenmt;

import com.example.trade_service.entity.Order;
import com.example.trade_service.entity.PayJuge;
import com.example.trade_service.service.OrderService;
import com.example.trade_service.service.PayJugeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class ReviewScheduler {

    @Autowired
    private OrderService orderService;
    @Autowired
    PayJugeService payJugeService;
    @Scheduled(cron = "0 0 1 * * ?")  // 每天凌晨执行,监测是否已经评价
    public void checkAndAddDefaultReviews() {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(3);
        List<Order> ordersWithoutReview = orderService.getOrdersWithoutReview(cutoffDate);

        for (Order order : ordersWithoutReview) {
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
}
