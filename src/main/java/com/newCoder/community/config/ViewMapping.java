package com.newCoder.community.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author lijie
 * @date 2022-11-10 22:40
 * @Desc
 */
@Configuration
public class ViewMapping implements WebMvcConfigurer {
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/login.html").setViewName("/site/login");
        registry.addViewController("/register.html").setViewName("/site/register");

    }
}
