package com.school.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
/**
 * @ClassName:WebConfig
 * @Author:DC
 * @Date:2024/6/30 11:47
 * @version:1.0
 * @Description:文件处理类
 */


@Configuration
public class WebConfig {
    @Bean
    public CommonsMultipartResolver multipartResolver() {
        CommonsMultipartResolver resolver = new CommonsMultipartResolver();
        resolver.setDefaultEncoding("UTF-8");
        // 设置最大上传文件大小 (100MB)
        resolver.setMaxUploadSize(100 * 1024 * 1024); // 100MB
        return resolver;
    }
}
