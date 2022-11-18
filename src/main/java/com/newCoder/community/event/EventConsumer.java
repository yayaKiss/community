package com.newCoder.community.event;

import com.alibaba.fastjson.JSON;
import com.newCoder.community.constant.TopicConstant;
import com.newCoder.community.entity.Event;
import com.newCoder.community.entity.Message;
import com.newCoder.community.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author lijie
 * @date 2022-11-18 23:00
 * @Desc
 */
@Component
@Slf4j
public class EventConsumer implements TopicConstant{
    private static final int SYSTEM_USER_ID = 1;

    @Autowired
    private MessageService messageService;

    @KafkaListener(topics = {COMMENT_TOPIC,LIKE_TOPIC,FOLLOW_TOPIC})
    public void consume(ConsumerRecord record){
        if(record == null || record.value() == null){
            log.info("消息的内容为空");
            return;
        }
        Event event = JSON.parseObject(record.value().toString(), Event.class);
        if(event == null){
            log.info("消息的实体类型不正确");
            return;
        }
        Message message = new Message();
        //系统用户
        message.setFromId(SYSTEM_USER_ID);
        message.setToId(event.getEntityUserId());
        message.setConversationId(event.getTopic());
        message.setStatus(0);
        message.setCreateTime(new Date());

        //用户xxx（评论，点赞，关注）了你的xxx
        Map<String,Object> content = new HashMap<>();
        content.put("userId",event.getUserId());
        content.put("entityType",event.getEntityType());
        content.put("entityId",event.getEntityId());
        if(!event.getData().isEmpty()){
            for(Map.Entry<String,Object> entry : event.getData().entrySet()){
                content.put(entry.getKey(),entry.getValue());
            }
        }
        message.setContent(JSON.toJSONString(content));

        //将消息队列中的实体转为message后，存进数据库，页面使用
        messageService.addMessage(message);
    }
}
