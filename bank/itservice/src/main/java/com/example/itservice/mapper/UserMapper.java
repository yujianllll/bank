package com.example.itservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.itservice.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {

}
