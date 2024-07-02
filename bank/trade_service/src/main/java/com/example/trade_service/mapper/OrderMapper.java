package com.example.trade_service.mapper;

import com.example.trade_service.entity.Order;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author 蒋浩宇
* @description 针对表【order】的数据库操作Mapper
* @createDate 2024-07-01 11:37:17
* @Entity com.example.trade_service.entity.Order
*/
@Mapper
public interface OrderMapper extends BaseMapper<Order> {

}




