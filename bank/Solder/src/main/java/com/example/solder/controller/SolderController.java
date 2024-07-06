package com.example.solder.controller;


import com.example.school.dto.Result;
import com.example.solder.dto.OrderDetailDTO;
import com.example.solder.entity.Solder;
import com.example.solder.service.SolderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;


@Slf4j
@RestController
@RequestMapping("/solde")
public class SolderController {
    @Resource
    SolderService solderService;
    //总体分页查询
    @GetMapping("/page")
    public Result Page(@RequestParam("currentPage") int currentPage,@RequestParam("pagesize") int pagesize)
    {
        return Result.ok(solderService.getUserPage(currentPage,pagesize));
    }
    //查询最新消息
    @GetMapping("new")
    public Result newfind()
    {
        return Result.ok(solderService.getnew());
    }
    //查询热点信息
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
    //按id查询
    @GetMapping("/findbyid")
    public Result findbyid(@RequestParam("id") Long id)
    {
        return Result.ok(solderService.query().eq("id",id).one());
    }
    //扣减库存
    @PostMapping("/stock/deduct")
    public void deductStock(@RequestBody OrderDetailDTO items){
        solderService.deductStock(items);
    }
    //删除商品
    @PostMapping("delete")
    public Result deleteshop(@RequestParam("id") Long id)
    {
        boolean isd = solderService.removeById(id);
        if(isd)
        {
            return Result.ok();
        }else{
            return Result.fail("删除失败");
        }
    }
    //添加商品信息
    @PostMapping("/add")
    public Result add(@RequestBody Solder solder)
    {
        boolean isadd = solderService.save(solder);
        if(isadd)
        {
            return Result.ok("添加成功");
        }
        return Result.fail("添加失败");
    }
    //修改商品信息,主要是库存,和是否添加广告
    @PostMapping("/update")
    public Result uodateshop(@RequestBody Solder solder)
    {
        Solder solder1 = new Solder();
        if(solder.getIsAd()!=0)
        {
            solder1.setIsAd(solder.getIsAd());
        }
        if(!solder.getStock().equals(null))
        {
            solder1.setStock(solder.getStock());
        }
        solder1.setId(solder.getId());
        try {
            boolean ifsoder = solderService.updateById(solder1);
            if(ifsoder){
                return Result.ok("修改成功");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("修改失败");
    }
    //设置限定商品
    @GetMapping("findspecial")
    public Result finds()
    {
        List<Solder> solders = solderService.query().eq("isAD",3).list();
        return Result.ok(solders);
    }
    //查询限定商品
}
