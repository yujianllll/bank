package com.example.bkapi.feign;

import com.example.solder.dto.Result;
import com.example.solder.dto.OrderDetailDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("Solder")
public interface SolderClient {
    @PostMapping("/solde/stock/deduct")
    void deductStock(@RequestBody OrderDetailDTO items);
    @GetMapping("/solde/findbyid")
    public Result findbyid(@RequestParam("id") Long id);
}
