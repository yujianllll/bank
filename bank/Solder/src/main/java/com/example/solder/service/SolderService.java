package com.example.solder.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.solder.dto.OrderDetailDTO;
import com.example.solder.entity.Solder;

import java.util.List;

/**
* @author 蒋浩宇
* @description 针对表【solder】的数据库操作Service
* @createDate 2024-06-30 10:48:01
*/
public interface SolderService extends IService<Solder> {
    public IPage<Solder> getUserPage(int currentPage, int pageSize);
    public List<Solder> getnew();
    public List<Solder> gethot();
    public List<Solder> findSoldersByNameOrCategory(String keyword);
    void deductStock(OrderDetailDTO items);
}
