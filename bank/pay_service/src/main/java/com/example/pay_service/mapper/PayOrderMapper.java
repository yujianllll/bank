package com.example.pay_service.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.pay_service.entity.PayOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
* @author 蒋浩宇
* @description 针对表【pay_order】的数据库操作Mapper
* @createDate 2024-07-01 11:50:18
* @Entity com.example.pay_service.entity.PayOrder
*/
@Mapper
public interface PayOrderMapper extends BaseMapper<PayOrder> {
    @Update("UPDATE pay_order SET status = 2 WHERE id = #{payId} and status = 1")
    int updateOrderStatus(@Param("id") String payId);
}




