package com.newCoder.community;

import com.newCoder.community.util.SensitiveWordFilter;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author lijie
 * @date 2022-11-13 21:11
 * @Desc
 */
@SpringBootTest
public class SensitiveWordTest {

    @Autowired
    private SensitiveWordFilter sensitiveWordFilter;
    @Test
    public void test(){
       String text = "这里可以赌博，可以嫖娼，可以吸毒，哈哈哈,  fabc ";
        String filter = sensitiveWordFilter.filter(text);
        System.out.println(filter);

    }
}
