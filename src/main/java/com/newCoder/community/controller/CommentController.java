package com.newCoder.community.controller;

import com.newCoder.community.annotation.LoginRequired;
import com.newCoder.community.constant.EntityConstant;
import com.newCoder.community.constant.TopicConstant;
import com.newCoder.community.entity.Comment;
import com.newCoder.community.entity.DiscussPost;
import com.newCoder.community.entity.Event;
import com.newCoder.community.entity.User;
import com.newCoder.community.event.EventProducer;
import com.newCoder.community.service.CommentService;
import com.newCoder.community.service.DiscussPostService;
import com.newCoder.community.util.HostHolder;
import com.newCoder.community.util.RedisKeyUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author lijie
 * @date 2022-11-15 09:04
 * @Desc
 */
@Slf4j
@Controller
@RequestMapping("/comment")
public class CommentController {
    @Autowired
    private CommentService commentService;
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private DiscussPostService discussPostService;
    @Autowired
    private EventProducer producer;
    @Autowired
    private RedisTemplate redisTemplate;

    @PostMapping("/add/{postId}")
    @LoginRequired
    public String addComment(@PathVariable("postId") int postId, Comment comment){
        User user = hostHolder.getValue();
        comment.setUserId(user.getId());
        //添加评论(并修改帖子评论数量)
        commentService.addComment(comment);

        //触发评论事件
        Event event = new Event();
        event = event.setUserId(user.getId())
                .setTopic(TopicConstant.COMMENT_TOPIC)
                .setEntityId(comment.getEntityId())
                .setEntityType(comment.getEntityType())
                .setData("postId",postId);
        if(comment.getEntityType() == EntityConstant.ENTITY_TYPE_POST){
            DiscussPost post = discussPostService.findDiscussPostDetail(comment.getEntityId());
            event.setEntityUserId(post.getUserId());
        }else if(comment.getEntityType() == EntityConstant.ENTITY_TYPE_COMMENT){
            Comment target = commentService.findCommentById(comment.getEntityId());
            event.setEntityUserId(target.getUserId());
        }
        producer.sendEvent(event);

        //触发发帖（帖子状态改变）事件（更新es中的帖子最新状态）
        if(comment.getEntityType() == EntityConstant.ENTITY_TYPE_POST){
            event = new Event().setUserId(user.getId())
                    .setTopic(TopicConstant.PUBLISH_COMMENT)
                    .setEntityType(EntityConstant.ENTITY_TYPE_POST)
                    .setEntityId(comment.getEntityId())
                    .setUserId(user.getId());

            producer.sendEvent(event);
        }

        //评论帖子，重新计算分数，将postId加入到redis集合中
        if(comment.getEntityType() == EntityConstant.ENTITY_TYPE_POST){
            String redisKey = RedisKeyUtils.getScoreKey();
            redisTemplate.opsForSet().add(redisKey,postId);
        }

        return "redirect:/discuss/detail/" + postId;
    }
}
