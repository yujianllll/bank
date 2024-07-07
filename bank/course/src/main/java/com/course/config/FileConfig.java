package com.course.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @ClassName:FileConfig
 * @Author:DC
 * @Date:2024/7/5 17:22
 * @version:1.0
 * @Description:文件映射
 */
@Configuration
public class FileConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        //和页面有关的静态目录都放在项目的static目录下
        registry.addResourceHandler("course/src/main/resources/static/**").
                addResourceLocations("file:course\\src\\main\\resources\\static\\");
    }
}
