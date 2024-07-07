package com.example.school.controller;


import com.example.school.dto.Result;
import com.example.school.entity.Cerity;
import com.example.school.service.ICerityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@Slf4j
@RestController
@RequestMapping("/identy")
public class UserIdentyController {
    @Resource
    ICerityService cerityService;
    //插入用户的申请
    @PostMapping("/insertidenty")
    public Result insertidenty(@RequestBody Cerity cerity)
    {
        cerityService.insertidenty(cerity);
        return Result.ok();
    }
    @GetMapping("/查看所有教师的申请")
    public Result seisok(@RequestBody Cerity cerity)
    {
        return Result.ok(cerityService.seisok(cerity));
    }
    //审核的结果
    public Result isok(@RequestBody Cerity cerity)
    {
        cerityService.isok(cerity);
        return Result.ok();
    }
}
