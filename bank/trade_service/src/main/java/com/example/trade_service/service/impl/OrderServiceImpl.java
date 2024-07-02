package com.example.trade_service.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.bkapi.feign.SolderClient;
import com.example.school.dto.Result;
import com.example.solder.dto.OrderDetailDTO;
import com.example.solder.entity.Solder;
import com.example.trade_service.entity.Order;
import com.example.trade_service.mapper.OrderMapper;
import com.example.trade_service.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
* @author 蒋浩宇
* @description 针对表【order】的数据库操作Service实现
* @createDate 2024-07-01 11:37:17
*/
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService{
    @Resource
    SolderClient solderClient;

    public OrderServiceImpl(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Result findtime(Order id) {
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
        return newid;
    }
}




