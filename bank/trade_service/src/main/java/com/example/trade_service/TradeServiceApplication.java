package com.example.trade_service;

import com.example.bkapi.config.DefaultFeignConfig;
import com.example.bkapi.feign.SolderClient;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableFeignClients(clients = {SolderClient.class},defaultConfiguration = DefaultFeignConfig.class)
public class TradeServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(TradeServiceApplication.class, args);
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
