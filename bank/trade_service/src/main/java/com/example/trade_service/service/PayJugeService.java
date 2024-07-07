package com.example.trade_service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.trade_service.entity.PayJuge;
import org.springframework.web.multipart.MultipartFile;

/**
* @author 蒋浩宇
* @description 针对表【pay_juge】的数据库操作Service
* @createDate 2024-07-05 10:51:23
*/
public interface PayJugeService extends IService<PayJuge> {
    public Double selectpi(Long id);
    public boolean insertp(PayJuge payJuge, MultipartFile file);
}
