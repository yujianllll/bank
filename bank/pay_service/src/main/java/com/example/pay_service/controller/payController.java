package com.example.pay_service.controller;


import com.example.pay_service.dto.PayApplyDTO;
import com.example.pay_service.service.PayOrderService;
import com.example.school.dto.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/pay")
public class payController {
    @Autowired
    PayOrderService payOrderService;
    //设置账单
    @PostMapping("/shezhi")
    public String applyPayOrder(@RequestBody PayApplyDTO applyDTO ,@RequestHeader(value = "user-info",required = false) String user){
        return payOrderService.applyPayOrder(applyDTO,user);
    }
    //点击按钮购买且查询账单功能
    @PostMapping("/isok")
    public Result isok(@RequestParam("pay_id") String pay_id)
    {
        payOrderService.tryPayOrderByBalance(pay_id);
        return Result.ok();
    }
    //取消订单
    @PostMapping("/cancle")
    public Result iscancle(@RequestParam("pay_id") String pay_id)
    {
        boolean isok = payOrderService.iscancle(pay_id);
        if(isok)
        {
            return Result.ok();
        }
        return Result.fail("更新失败");
    }
}
