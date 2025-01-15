package com.example.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

@Configuration
public class WebConfig implements  WebMvcConfigurer {

    @Resource
    private JwtInterceptor jwtInterceptor;

    // 加自定义拦截器JwtInterceptor，设置拦截规则
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor).addPathPatterns("/**")
                .excludePathPatterns("/")
                .excludePathPatterns("/login")
                .excludePathPatterns("/register")
                .excludePathPatterns("/api/send-code")  // 放行 /api/send-code 接口
                .excludePathPatterns("/api/register")   // 放行 /api/register 接口
                .excludePathPatterns("/files/**")
                .excludePathPatterns("/type/**")
                .excludePathPatterns("/notice/selectAll")
                .excludePathPatterns("/goods/**")
                .excludePathPatterns("/goods/**")
                .excludePathPatterns("/comment/selectByGoodsId/**")
                .excludePathPatterns(("/alipay/**"))
                .excludePathPatterns(("/seckillalipay/**"))
        ;
    }
}