package com.example.school.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.school.entity.Cerity;

import java.util.Set;

public interface ICerityService extends IService<Cerity> {
    void insertidenty(Cerity cerity);
    Set<String> seisok(Cerity cerity);
    void isok(Cerity cerity);
}
