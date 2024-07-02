package com.example.bkapi.feign;

import com.example.bkapi.dto.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("school")
public interface userClient {
    @GetMapping("/user/me")
    Result getUser(@RequestParam("phone") String phone);

    @GetMapping("/user/finddizhi")
    Result getdizhi();

    @PostMapping("/user/updatemoney")
    void updatemoney(@RequestParam("userId") Long userId, @RequestParam("totalFee") Double totalFee);
}
