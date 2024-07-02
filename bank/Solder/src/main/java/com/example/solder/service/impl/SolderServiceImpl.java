package com.example.solder.service.impl;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.solder.dto.OrderDetailDTO;
import com.example.solder.entity.Solder;
import com.example.solder.mapper.SolderMapper;
import com.example.solder.service.SolderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.example.solder.util.shopcontants.*;

/**
* @author 蒋浩宇
* @description 针对表【solder】的数据库操作Service实现
* @createDate 2024-06-30 10:48:01
*/
@Service
public class SolderServiceImpl extends ServiceImpl<SolderMapper, Solder> implements SolderService{

    @Autowired
    SolderMapper solderMapper;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    private static final ExecutorService CACHE_REBUILD_EXCUTOR = Executors.newFixedThreadPool(10);
    @Override
    public IPage<Solder> getUserPage(int currentPage, int pageSize) {
        Page<Solder> page = new Page<>(currentPage, pageSize);
        return solderMapper.selectPage(page);
    }

    @Override
    public List<Solder> getnew(){
        //判断redis是否有数据
        Map<Object,Object> shopMap = stringRedisTemplate.opsForHash().entries(CACHE_SHOP_KEY);
        if (shopMap.isEmpty()) {
            // 执行查询并获取结果
            List<Solder> solders = (List<Solder>) getSortedSolders();
            solders.forEach(solder -> {
                String id = String.valueOf(solder.getId());
                String solderjs = JSONUtil.toJsonStr(solder);
                stringRedisTemplate.opsForHash().put(CACHE_SHOP_KEY,id,solderjs);
                stringRedisTemplate.expire(CACHE_SHOP_KEY,LOCK_SHOP_TTL, TimeUnit.MINUTES);
            });
            return solders;
        }
        //5.判断是否过期
        Long time = stringRedisTemplate.getExpire(CACHE_SHOP_KEY, TimeUnit.SECONDS);
        System.out.println(time);
        if(time>300)
        {
            // 转换为 List<Solder>
            List<Solder> solderList = new ArrayList<>();
            for (Object value : shopMap.values()) {
                String jsonValue = (String) value;
                // 将 value 对象属性复制到 Solder 对象中
                Solder solder = JSONUtil.toBean(jsonValue,Solder.class);
                solderList.add(solder);
            }
            return solderList;
        }
        Boolean lock = tryLock(CACHE_SHOP_KEY);
        List<Solder> solders = getSortedSolders();
        if (lock != null && lock) {
            CACHE_REBUILD_EXCUTOR.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        // 获取锁成功，可以执行数据存储操作
                        for (Solder solder : solders) {
                            String id = String.valueOf(solder.getId());
                            String solderjs = JSONUtil.toJsonStr(solder);
                            stringRedisTemplate.opsForHash().put(CACHE_SHOP_KEY, id, solderjs);
                            stringRedisTemplate.expire(CACHE_SHOP_KEY, LOCK_SHOP_TTL, TimeUnit.MINUTES);
                        }
                        // 设置过期时间，模拟锁的自动释放
                        stringRedisTemplate.expire(CACHE_SHOP_KEY, LOCK_SHOP_TTL, TimeUnit.MINUTES);
                    } finally {
                        // 释放锁
                        stringRedisTemplate.opsForHash().delete(CACHE_SHOP_KEY, "lock");
                    }
                }
            });
        } else {
            // 获取锁失败，可以处理获取锁失败的逻辑，例如等待重试
            try {
                Thread.sleep(20);
                getnew();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("获取锁失败，无法执行操作");
        }
        return solders;
        //如果快要逻辑过期了
        //如果redis不见了或者超过逻辑过期设置的时间，就会缓存重建

      // 执行查询并获取结果
    }

    @Override
    public List<Solder> gethot() {
//        List<Solder> solders = query().list();
//        ZSetOperations<String, String> zSetOps = stringRedisTemplate.opsForZSet();
        Set<String> range = stringRedisTemplate.opsForZSet().reverseRange(CACHE_HOT_SHOP_KEY, 0, 19);
        List<Solder> solderList = new ArrayList<>();
        for (Object value : range) {
            String jsonValue = (String) value;
            // 将 value 对象属性复制到 Solder 对象中
            Solder solder = JSONUtil.toBean(jsonValue,Solder.class);
            solderList.add(solder);
        }
        return solderList;
//        for(Solder solder:solders)
//        {
//            String solderjs = JSONUtil.toJsonStr(solder);
//            double score = Double.parseDouble(solder.getIsAd() * 500 + solder.getSold());
//            zSetOps.add(CACHE_HOT_SHOP_KEY, solderjs, score);
//        }
//        return solders;
    }

    @Override
    public List<Solder> findSoldersByNameOrCategory(String keyword) {
        QueryWrapper<Solder> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("name", keyword).or().like("category", keyword).or().like("brand",keyword).last("ORDER BY isAD DESC");  // 同时进行名称和种类的模糊查询
        return solderMapper.selectList(queryWrapper);
    }

    @Override
    @Transactional
    public void deductStock(OrderDetailDTO items) {
        try {
            int updatedRows = solderMapper.updateStock(items.getItemId(), items.getNum());
            if (updatedRows == 0) {
                throw new RuntimeException("库存不足或商品不存在");
            }
        } catch (Exception e) {
            log.error("更新库存异常", e);
            throw new RuntimeException("更新库存失败", e);
        }
    }


    private  boolean tryLock(String key)
    {
        Boolean flag = stringRedisTemplate.opsForHash().putIfAbsent(key, "lock", "true");
        return BooleanUtil.isTrue(flag);
    }
    private List<Solder> getSortedSolders() {
        QueryWrapper<Solder> queryWrapper = new QueryWrapper<>();

        // 添加自定义的排序 SQL 片段和限制结果数量
        queryWrapper.last("ORDER BY isAD DESC, createdTime ASC ,sold DESC LIMIT 20");

        // 执行查询并获取结果
        List<Solder> solderList = solderMapper.selectList(queryWrapper);

        return solderList;
    }
}




