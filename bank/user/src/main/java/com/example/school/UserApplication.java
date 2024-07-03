package com.example.school;

import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

@EnableDiscoveryClient
@SpringBootApplication
@EnableFeignClients
public class UserApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserApplication.class, args);
    }
    //转json到rabbitmq中去
    @Bean
    public MessageConverter jacksonMessageConvertor(){
        Jackson2JsonMessageConverter jjmc = new Jackson2JsonMessageConverter();
        jjmc.setCreateMessageIds(true);//产生id
        return jjmc;
    }
}
