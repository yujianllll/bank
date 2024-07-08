package com.course;

import com.example.bkapi.config.DefaultFeignConfig;
import com.example.bkapi.feign.userClient;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @ClassName:${NAME}
 * @Author:DC
 * @Date:${DATE} ${TIME}
 * @version:${VERSION}
 * @Description:${DESCRIPTION}
 */
@EnableDiscoveryClient
@SpringBootApplication
@MapperScan("com.course.mapper") // 扫描Mapper接口所在的包
@EnableFeignClients(clients = {userClient.class},defaultConfiguration = DefaultFeignConfig.class)
public class CourseApplication {
    public static void main(String[] args) {
        SpringApplication.run(CourseApplication.class, args);
    }
}