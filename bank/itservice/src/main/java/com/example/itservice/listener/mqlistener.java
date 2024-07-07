package com.example.itservice.listener;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.example.itservice.entity.User;
import com.example.itservice.service.iml.UserServiceImpl;
import com.example.school.dto.LoginFormDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class mqlistener {
    @Autowired
    UserServiceImpl userService;

    @RabbitListener(bindings = @QueueBinding(//这种命令方法很简单
            value = @Queue(name = "find.queue1", durable = "true"),
            exchange = @Exchange(name = "finduser", type = ExchangeTypes.FANOUT)
    ))
    public void listenDirectQueue1(LoginFormDTO loginFormDTO) throws InterruptedException {
        if(loginFormDTO.getMaxchange()>10)
        {
            UpdateWrapper<User> wrapper = new UpdateWrapper<>();
            wrapper.eq("maxchange",loginFormDTO.getMaxchange())
                    .set("phone",loginFormDTO.getPhone());
            boolean updated = userService.update(null,wrapper);
            if(updated)
            {
                System.out.println("修改失败");
            }
            else{
                System.out.println("修改成功");
            }
        }
        else
        {
            System.out.println(loginFormDTO.getNewphone());
        }
    }
}
