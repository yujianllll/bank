package com.example.itservice.service.iml;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.itservice.entity.User;
import com.example.itservice.mapper.UserMapper;
import com.example.itservice.service.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

}
