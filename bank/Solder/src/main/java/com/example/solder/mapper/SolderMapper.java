package com.example.solder.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.solder.entity.Solder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
* @author 蒋浩宇
* @description 针对表【solder】的数据库操作Mapper
* @createDate 2024-06-30 10:48:01
* @Entity com.example.solder.entity.Solder
*/
@Mapper
public interface SolderMapper extends BaseMapper<Solder> {
    @Select("SELECT * FROM solder")
    IPage<Solder> selectPage(Page<?> page);
    @Update("UPDATE solder SET stock = stock - #{num},sold = sold +  #{num} WHERE id = #{itemId} AND stock >= #{num}")
    int updateStock(Long itemId, Integer num);
}




