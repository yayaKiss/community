package com.newCoder.community.dao;

import com.newCoder.community.entity.LoginTicket;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;

/**
 * @author lijie
 * @date 2022-11-12 09:30
 * @Desc
 */
@SpringBootTest
public class LoginTicketTest {
    @Autowired
    private LoginTicketMapper loginTicketMapper;
    @Test
    public void insertTest(){
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(115);
        loginTicket.setTicket("LKSHADUOhkjdaosuw56541321");
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date());
        int row = loginTicketMapper.insertLoginTicket(loginTicket);
        System.out.println(row);
    }
    @Test
    public void selectTest(){
        System.out.println(loginTicketMapper.selectByTicket("LKSHADUOhkjdaosuw56541321"));
    }
    @Test
    public void updateTest(){
        int row = loginTicketMapper.updateStatus("LKSHADUOhkjdaosuw56541321", 1);
        System.out.println(row);
    }
}
