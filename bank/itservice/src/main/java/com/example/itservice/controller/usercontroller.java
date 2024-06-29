package com.example.itservice.controller;

import com.example.bkapi.dto.Result;
import com.example.bkapi.feign.userClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/find")
public class usercontroller {
    @Autowired
    private final userClient userclient;


    @GetMapping("/me")
    public Result me(@RequestParam("phone") String phone) {
        // 调用 Feign 客户端接口来获取用户信息
        return userclient.getUser(phone);
    }
    @GetMapping("list")
    public Result listf(@RequestHeader(value = "user-info",required = false) String user)
    {
        System.out.println(user);
        return null;
    }
}
