package com.newCoder.community;

import com.newCoder.community.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author lijie
 * @date 2022-11-16 18:03
 * @Desc
 */
@SpringBootTest
public class RedisTemplateTest {
    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void test(){
        User user = new User();
        user.setId(1);
        user.setCreateTime(new Date());
        user.setStatus(1);
        user.setPassword("1654anfbkjajguiasddsf545");
        user.setUsername("ABCD");

        redisTemplate.opsForValue().set("test-user",user,10, TimeUnit.SECONDS);

    }
}
