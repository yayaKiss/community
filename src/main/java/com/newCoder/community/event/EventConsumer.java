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

    //消费主题事件，转为  系统通知事件存进数据库
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

    //消费发帖（帖子发生改变）事件 --- 》 更新es中帖子的最新状态
    @KafkaListener(topics = {TopicConstant.PUBLISH_COMMENT,TopicConstant.POST_TOP,TopicConstant.POST_WONDERFUL,TopicConstant.POST_SCORE})
    public void handlePublishMessage(ConsumerRecord record){
        if(record == null || record.value() == null){
            log.info("消息的内容为空");
            return;
        }

        Event event = JSON.parseObject(record.value().toString(), Event.class);
        if(event == null){
            log.info("消息的实体类型不正确");
            return;
        }
        //数据库查询状态改变的帖子
        int postId = event.getEntityId();
        DiscussPost post = discussPostService.findDiscussPostDetail(postId);

        //重新将数据添加到数据库中
        elasticSearchService.saveDiscussPost(post);

    }

    @KafkaListener(topics = TopicConstant.POST_DELETE)
    public void handleDeletePost(ConsumerRecord record){
        if(record == null || record.value() == null){
            log.info("消息的内容为空");
            return;
        }

        Event event = JSON.parseObject(record.value().toString(), Event.class);
        if(event == null){
            log.info("消息的实体类型不正确");
            return;
        }

        elasticSearchService.deleteDiscussPost(event.getEntityId());
    }

    @KafkaListener(topics = TopicConstant.HTML_SHARE)
    public void handleShare(ConsumerRecord record){
        if(record == null || record.value() == null){
            log.info("消息的内容为空");
            return;
        }

        Event event = JSON.parseObject(record.value().toString(), Event.class);
        if(event == null){
            log.info("消息的实体类型不正确");
            return;
        }
        String htmlUrl = (String) event.getData().get("htmlUrl");
        String fileName = (String) event.getData().get("fileName");
        String suffix = (String) event.getData().get("suffix");

        String cmd = command + " --quality 75 "
                + htmlUrl + " " + imageStorage + "/" + fileName + suffix;
        try {
            Runtime.getRuntime().exec(cmd);
            log.info("生成长图成功: " + cmd);
        } catch (IOException e) {
            log.error("生成长图失败: " + e.getMessage());
        }

        //启动一个定时线程，监视生成图片
        UploadTask task = new UploadTask(fileName,suffix);
        Future future = taskScheduler.scheduleAtFixedRate(task, 500);
        task.setFuture(future);

    }

    class UploadTask implements Runnable{
        //文件名称
        private String fileName;
        //文件后缀
        private String suffix;
        //传入future，可以终止任务
        private Future future;
        //定时器开始时间
        public long startTime;
        //任务上传次数
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
            // 生成失败
            if (System.currentTimeMillis() - startTime > 30000) {
                log.error("执行时间过长,终止任务:" + fileName);
                future.cancel(true);
                return;
            }
            // 上传失败
            if (uploadTimes >= 3) {
                log.error("上传次数过多,终止任务:" + fileName);
                future.cancel(true);
                return;
            }

            String path = imageStorage + "/" + fileName + suffix;
            File file = new File(path);
            if (file.exists()) {
                log.info(String.format("开始第%d次上传[%s].", ++uploadTimes, fileName));
                // 设置响应信息
                StringMap policy = new StringMap();
                policy.put("returnBody", JSON.toJSONString(JsonResult.ok()));
                // 生成上传凭证
                Auth auth = Auth.create(accessKey, secretKey);
                String uploadToken = auth.uploadToken(shareBucketName, fileName, 3600, policy);
                // 指定上传机房
                UploadManager manager = new UploadManager(new Configuration(Zone.zone2()));
                try {
                    // 开始上传图片
                    Response response = manager.put(
                            path, fileName, uploadToken, null, "image/png", false);
                    // 处理响应结果
                    JSONObject json = JSONObject.parseObject(response.bodyString());
                    if (json == null || json.get("code") == null || !json.get("code").toString().equals("200")) {
                        log.info(String.format("第%d次上传失败[%s].", uploadTimes, fileName));
                    } else {
                        log.info(String.format("第%d次上传成功[%s].", uploadTimes, fileName));
                        future.cancel(true);
                    }
                } catch (QiniuException e) {
                    log.info(String.format("第%d次上传失败[%s].", uploadTimes, fileName));
                }
            } else {
                log.info("等待图片生成[" + fileName + "].");
            }
        }
    }
}
