package com.example.school.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.school.dto.Result;
import com.example.school.dto.LoginFormDTO;
import com.example.school.entity.User;

import javax.servlet.http.HttpSession;

public interface IUserService extends IService<User> {
    public Result sendCode(String phone, HttpSession session);
    public Result login(LoginFormDTO loginForm, HttpSession session);
    public Result loginbypassword(LoginFormDTO loginFormDTO);
    public Result refindphone(LoginFormDTO loginFormDTO,HttpSession session);
    public void deductMoney(Long userId, Double totalFee);
    public void updateCredit(Long userId, Double totalFee);
}
