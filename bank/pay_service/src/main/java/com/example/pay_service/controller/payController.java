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
    @PostMapping("/shezhi")
    public String applyPayOrder(@RequestBody PayApplyDTO applyDTO){
        return payOrderService.applyPayOrder(applyDTO);
    }
    @PostMapping("/isok")
    public Result isok(@RequestParam("pay_id") String pay_id)
    {
        payOrderService.tryPayOrderByBalance(pay_id);
        return Result.ok();
    }
}
