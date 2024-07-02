package com.example.trade_service.controller;

import com.example.school.dto.Result;
import com.example.solder.dto.OrderDetailDTO;
import com.example.trade_service.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/trade")
public class orderController {
    @Autowired
    OrderService orderService;

    @GetMapping("/followid")
    public Result find(@RequestParam("id") String id)
    {
        return Result.ok(orderService.query().eq("id",id).one());
    }
    @PostMapping("/create")
    public String createOrder(@RequestBody OrderDetailDTO detailDTO,
                            @RequestHeader(value = "user-info",required = false) String user){
        return orderService.createOrder(detailDTO,user);
    }
}
