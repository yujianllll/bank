package com.example.school.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.school.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    @Update("UPDATE bk_user SET money = money - ${totalFee} WHERE id = #{userId}")
    int updateUserMoney(@Param("userId") Long userId, @Param("totalFee") Double totalFee);

    @Update("UPDATE bk_user SET credit = credit - ${totalFee} WHERE id = #{userId}")
    int updateUserCredit(@Param("userId") Long userId, @Param("totalFee") Double totalFee);
}
