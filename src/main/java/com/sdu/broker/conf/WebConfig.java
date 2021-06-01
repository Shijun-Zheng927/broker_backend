package com.sdu.broker.conf;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        System.out.println("shengxiao");
        String path = "D:/IDEA/broker/src/main/resources/static/img/";
        registry.addResourceHandler("/img/**").addResourceLocations("file:" + path);
        String path1 = "D:/IDEA/broker/src/main/resources/static/avatar/";
        registry.addResourceHandler("/avatar/**").addResourceLocations("file:" + path1);
    }
}
