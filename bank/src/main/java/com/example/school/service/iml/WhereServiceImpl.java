package com.example.school.service.iml;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.school.entity.Where;
import com.example.school.mapper.WhereMapper;
import com.example.school.service.IWhereService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class WhereServiceImpl extends ServiceImpl<WhereMapper, Where> implements IWhereService {
}
