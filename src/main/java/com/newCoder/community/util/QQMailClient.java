package com.newCoder.community.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;


/**
 * @author lijie
 * @date 2022-11-10 16:38
 * @Desc
 */
@Component
@Slf4j
public class QQMailClient {

    @Autowired
    private JavaMailSender mailSender;

    /**
     *
     * @param to  发送目标
     * @param subject   主题
     * @param content   内容
     */
    @Value("${spring.mail.username}")
    private String from;

    public void sendMessage(String to,String subject,String content){
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message);
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content,true);

            mailSender.send(helper.getMimeMessage());
            log.info("邮箱验证码发送成功!");
        } catch (MessagingException e) {
            log.error("发送邮件失败!" + e.getMessage());
        }
    }

}
