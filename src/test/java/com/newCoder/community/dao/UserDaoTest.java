package com.newCoder.community.dao;

import com.newCoder.community.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;

/**
 * @author lijie
 * @date 2022-11-09 13:35
 * @Desc
 */
@SpringBootTest
public class UserDaoTest {
    @Autowired
    private UserMapper userMapper;

    @Test
    public void insertUser(){
        User user = new User();
        user.setUsername("abcdefg");
        user.setPassword("123456");
        user.setEmail("1234@qq.com");
        user.setSalt("ajskf6");
        user.setHeaderUrl("http://www.nowcoder.com/101.png");
        user.setCreateTime(new Date());

        int row = userMapper.insertUser(user);
        System.out.println(row);
    }
    @Test
    public void selectUser(){
        User user = userMapper.selectById(1);
        System.out.println(user);

        user = userMapper.selectByName("dasffas");
        System.out.println(user);

        user = userMapper.selectByEmail("123456@qq.com");
        System.out.println(user);

    }

    @Test
    public void updateUser(){
        int row = userMapper.updatePassword(1,"666666");
        System.out.println(row);

        row = userMapper.updateHeaderUrl(1,"http://www.123.png");
        System.out.println(row);

        row = userMapper.updateStatus(1,2);
        System.out.println(row);
    }

}
