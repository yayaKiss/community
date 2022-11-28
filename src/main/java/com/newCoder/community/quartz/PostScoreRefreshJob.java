package com.newCoder.community.quartz;

import com.newCoder.community.constant.EntityConstant;
import com.newCoder.community.constant.TopicConstant;
import com.newCoder.community.entity.DiscussPost;
import com.newCoder.community.entity.Event;
import com.newCoder.community.event.EventProducer;
import com.newCoder.community.service.CommentService;
import com.newCoder.community.service.DiscussPostService;
import com.newCoder.community.service.ElasticSearchService;
import com.newCoder.community.service.LikeService;
import com.newCoder.community.util.RedisKeyUtils;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author lijie
 * @date 2022-11-23 08:12
 * @Desc
 */
@Slf4j
public class PostScoreRefreshJob implements Job {
    @Autowired
    private DiscussPostService discussPostService;
    @Autowired
    private LikeService likeService;
    @Autowired
    private EventProducer producer;
    @Autowired
    private RedisTemplate redisTemplate;

    private static final Date epoch;
    static {
        try {
            epoch = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2014-08-01 00:00:00");
        } catch (ParseException e) {
            throw new RuntimeException("初始化牛客纪元时间失败!");
        }
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        //获取分数集合的所有帖子
        String redisKey = RedisKeyUtils.getScoreKey();
        BoundSetOperations operations = redisTemplate.boundSetOps(redisKey);

        if(operations.size() == 0){
            log.info("[任务取消]，没有需要刷新的帖子!");
            return;
        }

        log.info("[任务开始]，正在刷新帖子分数，数量：{}，开始时间：{}",operations.size(),new Date());
        //更新每个帖子的分数
        while(operations.size() > 0){
            refresh((Integer)operations.pop());
        }
        log.info("[任务结束]，刷新帖子分数完成，结束时间：{}",new Date());

    }

    private void refresh(Integer postId) {
        DiscussPost post = discussPostService.findDiscussPostDetail(postId);
        if(post == null || post.getStatus() == 2){
            log.error("帖子被删除,id:{}",postId);
            return;
        }

        boolean isWonderful = post.getStatus() == 1;
        int comment = post.getCommentCount();
        int like = (int) likeService.findEntityLikeCount(EntityConstant.ENTITY_TYPE_POST,postId);
        int w = (isWonderful ? 75 : 0) + comment * 10 + like * 2;
        //计算分数
        double score = Math.log10(Math.max(w,1)) +
                (post.getCreateTime().getTime() - epoch.getTime()) / (1000 * 3600 * 24);

        //更改分数
        discussPostService.updateScore(postId,score);

        //异步更改es中分数
        post.setScore(score);
        Event event = new Event()
                .setTopic(TopicConstant.POST_SCORE)
                .setEntityType(EntityConstant.ENTITY_TYPE_POST)
                .setEntityId(postId);
        producer.sendEvent(event);
    }

}
