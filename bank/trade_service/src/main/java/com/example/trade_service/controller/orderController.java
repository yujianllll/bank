package com.example.trade_service.controller;

import com.example.school.dto.Result;
import com.example.solder.dto.OrderDetailDTO;
import com.example.trade_service.entity.Order;
import com.example.trade_service.entity.PayJuge;
import com.example.trade_service.service.OrderService;
import com.example.trade_service.service.PayJugeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/trade")
public class orderController {
    @Autowired
    OrderService orderService;
    @Autowired
    PayJugeService payJugeService;
    //找到订单
    @GetMapping("/followid")
    public Result find(@RequestParam("id") String id)
    {
        return Result.ok(orderService.query().eq("id",id).one());
    }
    //找到用户所有的订单
    @GetMapping("/findall")
    public Result findall(@RequestHeader(value = "user-info",required = false) String user)
    {
        List<Order> orderList = orderService.query().eq("user_id",Long.parseLong(user)).list();
        return Result.ok(orderList);
    }
    //找到所有的订单
    @GetMapping("/querye")
    public Result querye()
    {
        List<Order> orderList = orderService.query().list();
        return Result.ok(orderList);
    }
    //创建订单
    @PostMapping("/create")
    public String createOrder(@RequestBody OrderDetailDTO detailDTO,
                            @RequestHeader(value = "user-info",required = false) String user){
        return orderService.createOrder(detailDTO,user);
    }
    //创建特殊订单
    @PostMapping("/creatspecial")
    public String createOrderspecial(@RequestBody OrderDetailDTO detailDTO,
                              @RequestHeader(value = "user-info",required = false) String user){
        return orderService.createOrders(detailDTO,user);
    }
    //查看店里总评
    @GetMapping("selectpi")
    public Result selectpi(@RequestParam("solder_id") Long id)
    {
        return Result.ok(payJugeService.selectpi(id));
    }
    //添加评价
    @PostMapping("insertp")
    public Result insertp(@ModelAttribute PayJuge payJuge, @RequestParam("file") MultipartFile file)
    {
        if(payJugeService.insertp(payJuge,file))
        {
            return Result.ok();
        }
        return Result.fail("评论失败");
    }
    //管理员设置发货
    @PostMapping("fahuo")
    public Result fahuo(@RequestBody Order order)
    {
        orderService.lambdaUpdate()
                .set(Order::getStatus, 3)
                .eq(Order::getId, order.getId())
                .eq(Order::getStatus, 2)
                .update();
        return Result.ok();
    }
    //用户设置收货
    @PostMapping("shouhuo")
    public Result shouhuo(@RequestBody Order order)
    {
        orderService.lambdaUpdate()
                .set(Order::getStatus, 4)
                .eq(Order::getId, order.getId())
                .eq(Order::getStatus, 3)
                .update();
        return Result.ok();
    }
}
