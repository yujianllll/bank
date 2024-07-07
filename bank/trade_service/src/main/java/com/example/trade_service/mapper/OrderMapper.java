package com.example.trade_service.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.trade_service.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

/**
* @author 蒋浩宇
* @description 针对表【order】的数据库操作Mapper
* @createDate 2024-07-01 11:37:17
* @Entity com.example.trade_service.entity.Order
*/
@Mapper
public interface OrderMapper extends BaseMapper<Order> {
    @Select("SELECT o.* FROM order o LEFT JOIN pay_juge r ON o.id = r.pay_id WHERE r.pay_id IS NULL AND o.paySuccessTime <= #{cutoffDate}")
    List<Order> selectOrdersWithoutReview(@Param("cutoffDate") LocalDateTime cutoffDate);
}




