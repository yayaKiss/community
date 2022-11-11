package com.newCoder.community.config;

import com.google.code.kaptcha.Producer;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.Properties;

/**
 * @author lijie
 * @date 2022-11-11 17:59
 * @Desc
 */
@Configuration
public class KaptchaConfig {
    @Bean
    public Producer getKaptcha(){
        DefaultKaptcha kaptcha = new DefaultKaptcha();
        Properties properties = new Properties();
        try {
            properties.load(KaptchaConfig.class.getResourceAsStream("/kaptcha.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Config config = new Config(properties);
        kaptcha.setConfig(config);
        return kaptcha;
    }
}
