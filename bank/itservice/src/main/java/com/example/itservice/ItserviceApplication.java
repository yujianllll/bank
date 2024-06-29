package com.example.itservice;

import com.example.bkapi.config.DefaultFeignConfig;
import com.example.bkapi.feign.userClient;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
@EnableDiscoveryClient
@EnableFeignClients(clients = {userClient.class},defaultConfiguration = DefaultFeignConfig.class)
//@MapperScan("com.example.itservice.mapper")
@SpringBootApplication
public class ItserviceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ItserviceApplication.class, args);
    }
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
    @Bean
    public MessageConverter jacksonMessageConvertor(){
        return new Jackson2JsonMessageConverter();
    }
}
