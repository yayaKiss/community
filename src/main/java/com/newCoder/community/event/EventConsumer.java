package com.newCoder.community.event;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.newCoder.community.constant.TopicConstant;
import com.newCoder.community.entity.DiscussPost;
import com.newCoder.community.entity.Event;
import com.newCoder.community.entity.Message;
import com.newCoder.community.service.DiscussPostService;
import com.newCoder.community.service.ElasticSearchService;
import com.newCoder.community.service.MessageService;
import com.newCoder.community.util.JsonResult;
import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

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

    @Autowired
    private ElasticSearchService elasticSearchService;

    @Autowired
    private DiscussPostService discussPostService;

    @Value("${wk.image.command}")
    private String command;
    @Value("${wk.image.storage}")
    private String imageStorage;


    @Value("${qiniu.key.access}")
    private String accessKey;
    @Value("${qiniu.key.secret}")
    private String secretKey;
    @Value("${qiniu.bucket.share.name}")
    private String shareBucketName;

    @Autowired
    private ThreadPoolTaskScheduler taskScheduler;

    //???????????????????????????  ?????????????????????????????????
    @KafkaListener(topics = {COMMENT_TOPIC,LIKE_TOPIC,FOLLOW_TOPIC})
    public void consume(ConsumerRecord record){
        if(record == null || record.value() == null){
            log.info("?????????????????????");
            return;
        }
        Event event = JSON.parseObject(record.value().toString(), Event.class);
        if(event == null){
            log.info("??????????????????????????????");
            return;
        }
        Message message = new Message();
        //????????????
        message.setFromId(SYSTEM_USER_ID);
        message.setToId(event.getEntityUserId());
        message.setConversationId(event.getTopic());
        message.setStatus(0);
        message.setCreateTime(new Date());

        //??????xxx???????????????????????????????????????xxx
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

        //?????????????????????????????????message????????????????????????????????????
        messageService.addMessage(message);
    }

    //?????????????????????????????????????????? --- ??? ??????es????????????????????????
    @KafkaListener(topics = {TopicConstant.PUBLISH_COMMENT,TopicConstant.POST_TOP,TopicConstant.POST_WONDERFUL,TopicConstant.POST_SCORE})
    public void handlePublishMessage(ConsumerRecord record){
        if(record == null || record.value() == null){
            log.info("?????????????????????");
            return;
        }

        Event event = JSON.parseObject(record.value().toString(), Event.class);
        if(event == null){
            log.info("??????????????????????????????");
            return;
        }
        //????????????????????????????????????
        int postId = event.getEntityId();
        DiscussPost post = discussPostService.findDiscussPostDetail(postId);

        //????????????????????????????????????
        elasticSearchService.saveDiscussPost(post);

    }

    @KafkaListener(topics = TopicConstant.POST_DELETE)
    public void handleDeletePost(ConsumerRecord record){
        if(record == null || record.value() == null){
            log.info("?????????????????????");
            return;
        }

        Event event = JSON.parseObject(record.value().toString(), Event.class);
        if(event == null){
            log.info("??????????????????????????????");
            return;
        }

        elasticSearchService.deleteDiscussPost(event.getEntityId());
    }

    @KafkaListener(topics = TopicConstant.HTML_SHARE)
    public void handleShare(ConsumerRecord record){
        if(record == null || record.value() == null){
            log.info("?????????????????????");
            return;
        }

        Event event = JSON.parseObject(record.value().toString(), Event.class);
        if(event == null){
            log.info("??????????????????????????????");
            return;
        }
        String htmlUrl = (String) event.getData().get("htmlUrl");
        String fileName = (String) event.getData().get("fileName");
        String suffix = (String) event.getData().get("suffix");

        String cmd = command + " --quality 75 "
                + htmlUrl + " " + imageStorage + "/" + fileName + suffix;
        try {
            Runtime.getRuntime().exec(cmd);
            log.info("??????????????????: " + cmd);
        } catch (IOException e) {
            log.error("??????????????????: " + e.getMessage());
        }

        //?????????????????????????????????????????????
        UploadTask task = new UploadTask(fileName,suffix);
        Future future = taskScheduler.scheduleAtFixedRate(task, 500);
        task.setFuture(future);

    }

    class UploadTask implements Runnable{
        //????????????
        private String fileName;
        //????????????
        private String suffix;
        //??????future?????????????????????
        private Future future;
        //?????????????????????
        public long startTime;
        //??????????????????
        public int uploadTimes;

        public UploadTask(String fileName, String suffix) {
            this.fileName = fileName;
            this.suffix = suffix;
            this.startTime = System.currentTimeMillis();
        }
        public void setFuture(Future future){
            this.future = future;
        }

        @Override
        public void run() {
            // ????????????
            if (System.currentTimeMillis() - startTime > 30000) {
                log.error("??????????????????,????????????:" + fileName);
                future.cancel(true);
                return;
            }
            // ????????????
            if (uploadTimes >= 3) {
                log.error("??????????????????,????????????:" + fileName);
                future.cancel(true);
                return;
            }

            String path = imageStorage + "/" + fileName + suffix;
            File file = new File(path);
            if (file.exists()) {
                log.info(String.format("?????????%d?????????[%s].", ++uploadTimes, fileName));
                // ??????????????????
                StringMap policy = new StringMap();
                policy.put("returnBody", JSON.toJSONString(JsonResult.ok()));
                // ??????????????????
                Auth auth = Auth.create(accessKey, secretKey);
                String uploadToken = auth.uploadToken(shareBucketName, fileName, 3600, policy);
                // ??????????????????
                UploadManager manager = new UploadManager(new Configuration(Zone.zone2()));
                try {
                    // ??????????????????
                    Response response = manager.put(
                            path, fileName, uploadToken, null, "image/png", false);
                    // ??????????????????
                    JSONObject json = JSONObject.parseObject(response.bodyString());
                    if (json == null || json.get("code") == null || !json.get("code").toString().equals("200")) {
                        log.info(String.format("???%d???????????????[%s].", uploadTimes, fileName));
                    } else {
                        log.info(String.format("???%d???????????????[%s].", uploadTimes, fileName));
                        future.cancel(true);
                    }
                } catch (QiniuException e) {
                    log.info(String.format("???%d???????????????[%s].", uploadTimes, fileName));
                }
            } else {
                log.info("??????????????????[" + fileName + "].");
            }
        }
    }
}
