package com.newCoder.community;

import com.newCoder.community.util.QQMailClient;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;


/**
 * @author lijie
 * @date 2022-11-10 16:13
 * @Desc
 */
@SpringBootTest
@Slf4j
public class SendMailTest {
    @Autowired
    private QQMailClient mailClient;
    @Autowired
    private TemplateEngine templateEngine;

    @Test
    public void mailClientTest(){
        mailClient.sendMessage("3568958570@qq.com","欢迎","Welcome to Chain");
    }

    @Test
    public void mailClientHTMLTest(){
        Context context = new Context();
        context.setVariable("username","电击小子");
        String content = templateEngine.process("/mail/activation.html", context);
        mailClient.sendMessage("3568958570@qq.com","欢迎",content);
    }

}
