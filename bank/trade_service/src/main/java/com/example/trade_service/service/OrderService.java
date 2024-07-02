package com.example.trade_service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.school.dto.Result;
import com.example.solder.dto.OrderDetailDTO;
import com.example.trade_service.entity.Order;

/**
* @author 蒋浩宇
* @description 针对表【order】的数据库操作Service
* @createDate 2024-07-01 11:37:17
*/
public interface OrderService extends IService<Order> {
    Result findtime(Order id);
    String createOrder(OrderDetailDTO detailDTO, String user);
}
