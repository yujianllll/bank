package com.school.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.school.entity.UserDTO;

/**
 * @ClassName:UserMapper
 * @Author:DC
 * @Date:2024/7/3 9:11
 * @version:1.0
 * @Description:用户信息
 */
public interface UserMapper extends BaseMapper<UserDTO> {
    UserDTO getUserById(Long id);
}
