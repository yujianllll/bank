package com.example.bkapi.config;

import com.example.school.dto.UserDTO;
import com.example.school.entity.UserHolder;
import feign.Logger;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;

;

public class DefaultFeignConfig {
    @Bean
    public Logger.Level feignLoggerLevel(){
        return Logger.Level.FULL;
    }
    @Bean
    public RequestInterceptor requestInterceptor() {
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate requestTemplate) {

                UserDTO user = UserHolder.getUser();
                if (user != null) {
                    requestTemplate.header("user-info", user.getId().toString());
                }
            }
        };
    }
}
