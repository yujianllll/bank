package com.example.trade_service.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.trade_service.entity.Order;
import com.example.trade_service.entity.PayJuge;
import com.example.trade_service.mapper.PayJugeMapper;
import com.example.trade_service.service.OrderService;
import com.example.trade_service.service.PayJugeService;
import com.example.trade_service.util.fileupdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

/**
* @author 蒋浩宇
* @description 针对表【pay_juge】的数据库操作Service实现
* @createDate 2024-07-05 10:51:23
*/
@Service
public class PayJugeServiceImpl extends ServiceImpl<PayJugeMapper, PayJuge> implements PayJugeService {
    @Autowired
    OrderService orderService;

    @Override//可以将热门商家的评价存储到redis里面
    public Double selectpi(Long id) {
        List<PayJuge> payJugeList = query().eq("solder_id",id).list();
        double rsocer = 0.0;
        for(PayJuge payJuge:payJugeList)
        {
            rsocer += payJuge.getJuge();
        }
        rsocer = rsocer/payJugeList.size();
        return rsocer;
    }

    @Override//这里可以想办法防止恶意刷差评
    public boolean insertp(PayJuge payJuge, MultipartFile file) {
        Order Order = orderService.query().eq("id",payJuge.getPayId()).
                ge("status",2).le("status",4).one();
        if(Order==null)
        {
            return false;
        }
        String image = "";
        fileupdate fileupdate1 = new fileupdate();
        if(file.isEmpty())
        {
           payJuge.setImage("");
        }
        else
        {
            String filePath = fileupdate1.saveFile(file,null);
            if(filePath.isEmpty())
            {
                throw new RuntimeException("上传图片失败");
            }
            payJuge.setImage(filePath);
        }
        payJuge.setCreateTime(Timestamp.valueOf(LocalDateTime.now()));
        save(payJuge);
        return true;
    }
}




