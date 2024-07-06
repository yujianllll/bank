package com.example.pay_service.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.bkapi.feign.userClient;
import com.example.pay_service.dto.PayApplyDTO;
import com.example.pay_service.entity.PayOrder;
import com.example.pay_service.entity.PayStatus;
import com.example.pay_service.mapper.PayOrderMapper;
import com.example.pay_service.service.PayOrderService;
import com.example.pay_service.util.MqContent;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
* @author 蒋浩宇
* @description 针对表【pay_order】的数据库操作Service实现
* @createDate 2024-07-01 11:50:18
*/
@Service
public class PayOrderServiceImpl extends ServiceImpl<PayOrderMapper, PayOrder> implements PayOrderService{

    @Resource
    userClient userclient;
    @Autowired
    RabbitTemplate rabbitTemplate;
    @Autowired
    PayOrderMapper payOrderMapper;
    @Override
    public String applyPayOrder(PayApplyDTO applyDTO) {
        // 1.幂等性校验
        PayOrder payOrder = checkIdempotent(applyDTO);
        // 2.返回结果
        return payOrder.getId().toString();
    }

    @Override
    public void tryPayOrderByBalance(String id) {
        PayOrder payOrder = query().eq("id",id).one();
        if(payOrder==null)
        {
            throw new RuntimeException("订单不存在");
        }
        if(!PayStatus.WAIT_BUYER_PAY.equalsValue((int) payOrder.getStatus()))
        {
            throw new RuntimeException("订单已处理");
        }
        //修改余额
        userclient.updatemoney(payOrder.getBizUserId(),payOrder.getAmount());
        //设置订单成功
        boolean isok = markPayOrderSuccess(id,LocalDateTime.now());
        if(isok)
        {
            try {
                rabbitTemplate.convertAndSend("pay.topic", "pay.success", payOrder.getBizOrderNo());
                rabbitTemplate.convertAndSend(MqContent.MQ_PAYDE,MqContent.DELAY_ORDER_ROUTING_KEY,payOrder.getBizOrderNo(), new MessagePostProcessor() {
                    @Override
                    public Message postProcessMessage(Message message) throws AmqpException {
                        message.getMessageProperties().setDelay(259200000);
                        return message;
                    }
                });
            } catch (AmqpException e) {
                log.error("支付成功，但是通知交易服务失败",  e);
            }
        }
        else
        {
            throw new RuntimeException("订单更新失败");
        }
    }

    @Override
    public boolean iscancle(String id) {
        int iis = payOrderMapper.updateOrderStatus(id);
        if(iis>0)
        {
            try {
                rabbitTemplate.convertAndSend("pay.topic", "pay.cancle", id);
            } catch (AmqpException e) {
                log.error("支付成功，但是通知交易服务失败",  e);
            }
            return true;
        }
        return false;
    }

    public boolean markPayOrderSuccess(String id, LocalDateTime successTime) {
        return lambdaUpdate()
                .set(PayOrder::getStatus, PayStatus.TRADE_SUCCESS.getValue())
                .set(PayOrder::getPaySuccessTime, successTime)
                .eq(PayOrder::getId, id)
                // 支付状态的乐观锁判断
                .in(PayOrder::getStatus, PayStatus.NOT_COMMIT.getValue(), PayStatus.WAIT_BUYER_PAY.getValue())
                .update();
    }

    private PayOrder checkIdempotent(PayApplyDTO applyDTO) {
        PayOrder oldOrder = query().eq("bizOrderNo",applyDTO.getBizOrderNo()).one();
        if(oldOrder==null)
        {
            PayOrder payOrder = buildPayOrder(applyDTO);
            String id = String.valueOf(applyDTO.getBizUserId())+String.valueOf(applyDTO.getAmount())+LocalDateTime.now().toString();
            String newid = id.replaceAll("[^\\d]", "");
            payOrder.setPayOrderNo(newid);
            payOrder.setId(newid);
            save(payOrder);
            return payOrder;
        }
        if(PayStatus.TRADE_SUCCESS.equalsValue((int) applyDTO.getStatus()))
        {
            throw new RuntimeException("订单已支付");
        }
        else if(PayStatus.TRADE_CLOSED.equalsValue((int) applyDTO.getStatus()))
        {
            throw new RuntimeException("订单已取消支付");
        }
        return oldOrder;
    }
    private PayOrder buildPayOrder(PayApplyDTO payApplyDTO)
    {
        // 1.数据转换
        PayOrder payOrder = BeanUtil.toBean(payApplyDTO, PayOrder.class);
        payOrder.setCreateTime(Timestamp.valueOf(LocalDateTime.now()));
        payOrder.setPayOverTime(Timestamp.valueOf(LocalDateTime.now().plusMinutes(120L)));
        payOrder.setStatus(PayStatus.WAIT_BUYER_PAY.getValue());
        payOrder.setBizUserId(payOrder.getBizUserId());
        return payOrder;
    }
}




