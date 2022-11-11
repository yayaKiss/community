package com.newCoder.community;

import com.newCoder.community.config.KaptchaConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.Properties;

/**
 * @author lijie
 * @date 2022-11-11 18:16
 * @Desc
 */
@SpringBootTest
public class readProperties {

    @Test
    public void testReadProperties(){
        Properties properties = new Properties();
        try {
            properties.load(this.getClass().getResourceAsStream("/kaptcha.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(properties.getProperty("kaptcha.image.height"));
        System.out.println(properties.getProperty("kaptcha.image.width"));
        System.out.println(properties.getProperty("kaptcha.textproducer.char.string"));
        System.out.println(properties.getProperty("kaptcha.noise.impl"));
    }

}
