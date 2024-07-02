package com.example.solder.controller;


import com.example.school.dto.Result;
import com.example.solder.dto.OrderDetailDTO;
import com.example.solder.service.SolderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;


@Slf4j
@RestController
@RequestMapping("/solde")
public class SolderController {
    @Resource
    SolderService solderService;
    @GetMapping("/page")
    public Result Page(@RequestParam("currentPage") int currentPage,@RequestParam("pagesize") int pagesize)
    {
        return Result.ok(solderService.getUserPage(currentPage,pagesize));
    }
    @GetMapping("new")
    public Result newfind()
    {
        return Result.ok(solderService.getnew());
    }
    @GetMapping("hot")
    public Result hot()
    {
        return Result.ok(solderService.gethot());
    }
    //模糊查询
    @GetMapping("/findmohu")
    public Result findmohu(@RequestParam("keyword") String keyword)
    {
        return Result.ok(solderService.findSoldersByNameOrCategory(keyword));
    }
    @GetMapping("/findbyid")
    public Result findbyid(@RequestParam("id") Long id)
    {
        return Result.ok(solderService.query().eq("id",id).one());
    }
    @PostMapping("/stock/deduct")
    public void deductStock(@RequestBody OrderDetailDTO items){
        solderService.deductStock(items);
    }
}
