package com.newCoder.community.event;

import com.alibaba.fastjson.JSON;
import com.newCoder.community.entity.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * @author lijie
 * @date 2022-11-18 22:53
 * @Desc
 */
@Component
public class EventProducer {
    @Autowired
    private KafkaTemplate kafkaTemplate;

    //（点赞、关注、评论后发送一条event到队列中）
    public void sendEvent(Event event){
        kafkaTemplate.send(event.getTopic(), JSON.toJSONString(event));
    }
}
