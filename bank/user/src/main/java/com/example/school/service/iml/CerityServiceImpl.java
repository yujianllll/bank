package com.example.school.service.iml;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.school.entity.Cerity;
import com.example.school.entity.User;
import com.example.school.mapper.CerityMapper;
import com.example.school.service.ICerityService;
import com.example.school.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.Set;
import java.util.UUID;

public class CerityServiceImpl extends ServiceImpl<CerityMapper, Cerity> implements ICerityService {
    @Autowired
    IUserService userService;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Override
    public void insertidenty(Cerity cerity) {
        // 生成一个 UUID 并取其 hashCode
        int hashCode = UUID.randomUUID().hashCode();

        // 取 hashCode 的绝对值，确保是正数
        if (hashCode < 0) {
            hashCode = -hashCode;
        }
        Long id = hashCode % 100000000000L;
        cerity.setId(id);
        cerity.setIss(0);
        String cerityis = JSONUtil.toJsonStr(cerity);
        stringRedisTemplate.opsForSet().add("cerity.set",cerityis);
        save(cerity);
    }

    @Override
    public Set<String> seisok(Cerity cerity) {
        return stringRedisTemplate.opsForSet().members("cerity.set");
    }

    @Override
    public void isok(Cerity cerity) {
        if(cerity.getIss()==2)
        {
            System.out.println("审核失败");
        }else if(cerity.getIss()==1)
        {
            userService.lambdaUpdate()
                    .set(User::getIdenty,1)
                    .eq(User::getId,cerity.getUser_id())
                    .eq(User::getIdenty,null)
                    .update();
        }
        cerity.setIss(0);
        stringRedisTemplate.opsForSet().remove("cerity.set",JSONUtil.toJsonStr(cerity));
    }

}
