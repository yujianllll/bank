package com.example.school.config;

import com.example.school.utils.RefreshTokenInterceptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

@ConditionalOnClass(DispatcherServlet.class)
@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 登录拦截器
//        registry.addInterceptor(new LoginInterceptor())//添加拦截器
//                .excludePathPatterns(
//                        "/shop/**",
//                        "/voucher/**",
//                        "/shop-type/**",
//                        "/upload/**",
//                        "/blog/hot",
//                        "/user/code",
//                        "/user/login"
//                ).order(1);
        // token刷新的拦截器
        registry.addInterceptor(new RefreshTokenInterceptor(stringRedisTemplate)).addPathPatterns("/**").order(0);//order越小执行优先级越高
    }
}
