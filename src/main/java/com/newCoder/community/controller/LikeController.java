package com.newCoder.community.controller;

import com.newCoder.community.constant.TopicConstant;
import com.newCoder.community.entity.Event;
import com.newCoder.community.entity.User;
import com.newCoder.community.event.EventProducer;
import com.newCoder.community.service.LikeService;
import com.newCoder.community.util.HostHolder;
import com.newCoder.community.util.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author lijie
 * @date 2022-11-16 21:21
 * @Desc
 */
@Controller
public class LikeController {
    @Autowired
    private LikeService likeService;
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private EventProducer producer;

    @PostMapping("/like")
    @ResponseBody
    public JsonResult like(int entityType,int entityId,int entityUserId,int postId){
        User user = hostHolder.getValue();
        if(user == null){
            return JsonResult.error("您还未登录");
        }
        JsonResult result = JsonResult.ok();
        //点赞
        likeService.like(user.getId(),entityType,entityId,entityUserId);
        //查询实体点赞数
        long likeCount = likeService.findEntityLikeCount(entityType,entityId);
        result.put("likeCount",likeCount);
        //查询用户对该实体的点赞状态
        int likeStatus = likeService.findEntityLikeStatus(user.getId(),entityType,entityId);
        result.put("likeStatus",likeStatus);

        if(likeStatus == 1){
            Event event = new Event();
            event = event.setUserId(user.getId())
                    .setEntityType(entityType)
                    .setEntityId(entityId)
                    .setEntityUserId(entityUserId)
                    .setTopic(TopicConstant.LIKE_TOPIC)
                    .setData("postId",postId);//到时候需要链到目标帖子

            producer.sendEvent(event);
        }

        return result;
    }
}
