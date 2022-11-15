package com.newCoder.community.dao;

import com.newCoder.community.entity.Message;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * @author lijie
 * @date 2022-11-15 13:31
 * @Desc
 */
@SpringBootTest
public class MessageMapperTest {
    @Autowired
    private MessageMapper messageMapper;
    @Test
    public void test(){
        int row = messageMapper.selectLetterCount("111_112");
        System.out.println(row);

        row = messageMapper.selectUnreadLetterCount(111, null);
        System.out.println(row);

        List<Message> messages = messageMapper.selectConversations(111, 0, 10);
        for(Message message : messages){
            System.out.println(message);
        }

        messages = messageMapper.selectLetters("111_112", 0, 10);
        for(Message message : messages){
            System.out.println(message);
        }

    }
}
