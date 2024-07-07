package com.example.trade_service.controller;

import com.example.school.dto.Result;
import com.example.trade_service.entity.PayJuge;
import com.example.trade_service.service.PayJugeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/tradeforrj")
public class jugeController {
    @Autowired
    PayJugeService payJugeService;

    @GetMapping("/selectbyhei")
    public Result selectbyhei(@RequestParam("solderId") String sid)
    {
        List<PayJuge> payJugeList= payJugeService.query().eq("solder_id",sid).last("ORDER BY juge DESC").list();
        return Result.ok(payJugeList);
    }
    @GetMapping("/selectbylow")
    public Result selectbylow(@RequestParam("solderId") String sid)
    {
        List<PayJuge> payJugeList= payJugeService.query().eq("solder_id",sid).last("ORDER BY juge ASC").list();
        return Result.ok(payJugeList);
    }
}
