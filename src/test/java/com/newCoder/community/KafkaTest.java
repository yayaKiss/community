package com.newCoder.community;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * @author lijie
 * @date 2022-11-18 20:18
 * @Desc
 */
@SpringBootTest
public class KafkaTest {
    @Autowired
    private KafkaProducer producer;
    @Test
    public void test(){
        producer.sendMessage("test","你好呀");
        producer.sendMessage("test","一起吃个饭呗");
        try {
            Thread.sleep(10 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}

@Component
class KafkaProducer{
    @Autowired
    private KafkaTemplate kafkaTemplate;

    public void sendMessage(String topic,String data){
        kafkaTemplate.send(topic,data);
    }
}

@Component
class KafkaConsumer{

    @KafkaListener(topics = {"test"})
    public void consumerMessage(ConsumerRecord consumerRecord){
        System.out.println(consumerRecord.value());
    }
}