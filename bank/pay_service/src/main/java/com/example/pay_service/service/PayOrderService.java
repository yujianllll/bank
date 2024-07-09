package com.example.pay_service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.pay_service.dto.PayApplyDTO;
import com.example.pay_service.entity.PayOrder;

/**
* @author 蒋浩宇
* @description 针对表【pay_order】的数据库操作Service
* @createDate 2024-07-01 11:50:18
*/
public interface PayOrderService extends IService<PayOrder> {
    String applyPayOrder(PayApplyDTO applyDTO, String user_id);
    public void tryPayOrderByBalance(String id);
    boolean iscancle(String id);
}
