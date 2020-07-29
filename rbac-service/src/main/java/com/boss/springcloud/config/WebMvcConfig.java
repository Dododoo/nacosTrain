package com.boss.springcloud.config;

import com.boss.springcloud.controller.intercepter.UserIntercepter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private UserIntercepter userIntercepter;


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //registry.addInterceptor(userIntercepter)
                //.excludePathPatterns("/**/*.css", "/**/*.js", "/**/*.png", "/**/*.jpg", "/**/*.jpeg")
                //.addPathPatterns("/user/*");

    }

}
