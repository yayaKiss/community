package com.newCoder.community.controller;

import com.newCoder.community.constant.EntityConstant;
import com.newCoder.community.constant.TopicConstant;
import com.newCoder.community.entity.Event;
import com.newCoder.community.entity.Page;
import com.newCoder.community.entity.User;
import com.newCoder.community.event.EventConsumer;
import com.newCoder.community.event.EventProducer;
import com.newCoder.community.service.FollowService;
import com.newCoder.community.service.UserService;
import com.newCoder.community.util.HostHolder;
import com.newCoder.community.util.JsonResult;
import com.newCoder.community.util.RedisKeyUtils;
import com.newCoder.community.vo.FollowVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;


/**
 * @author lijie
 * @date 2022-11-17 13:21
 * @Desc
 */
@Controller
public class FollowController {
    @Autowired
    private FollowService followService;
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private UserService userService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private EventProducer producer;

    @PostMapping("/follow")
    @ResponseBody
    public JsonResult follow(int entityType, int entityId, Model model){
        User user = hostHolder.getValue();
        if(user == null){
            return JsonResult.error("您未进行登录");
        }
        // 关注/取消关注
        followService.follow(user.getId(),entityType,entityId);

        //如果当前用户关注了某个实体，就发送系统通知
        if(followService.findUserFollowStatus(user.getId(),entityType,entityId)){
            Event event = new Event();
            event = event.setUserId(user.getId())
                    .setEntityType(entityType)
                    .setEntityId(entityId)
                    .setTopic(TopicConstant.FOLLOW_TOPIC)
                    .setEntityUserId(entityId);

            producer.sendEvent(event);
        }

        return JsonResult.ok();
    }

    @GetMapping("/followees/{userId}")
    public String getFollowees(@PathVariable("userId")int userId, Page page,Model model){
        User user = userService.findUserById(userId);
        if(user == null){
            throw new RuntimeException("用户不存在");
        }
        model.addAttribute("user",user);

        page.setLimit(5);
        page.setRows((int) followService.findEntityFolloweeCount(userId,EntityConstant.ENTITY_TYPE_USER));
        page.setPath("/followees/" + userId);

        Set<Integer> targetIds = followService.findUserFollowees(userId, page.getOffset(), page.getLimit());
        List<FollowVo> followVos = new ArrayList<>();
        if(targetIds != null){
            for(int targetId : targetIds){
                FollowVo vo = new FollowVo();
                User u = userService.findUserById(targetId);
                Double followTime = redisTemplate.opsForZSet().score(
                        RedisKeyUtils.getFolloweeKey(userId, EntityConstant.ENTITY_TYPE_USER), targetId);
                //当我当前登录用户而言 -->是否关注过他
                boolean followStatus = hasFollowed(targetId);
                vo.setUserId(u.getId());
                vo.setUsername(u.getUsername());
                vo.setHeaderUrl(u.getHeaderUrl());
                vo.setFollowTime(new Date(followTime.longValue()));
                vo.setFollowStatus(followStatus);

                followVos.add(vo);
            }
        }
        model.addAttribute("followVos",followVos);
        return "/site/followee";
    }
    private boolean hasFollowed(int targetId){
        User user = hostHolder.getValue();
        if(user == null){
            return false;
        }
        return followService.findUserFollowStatus(user.getId(),EntityConstant.ENTITY_TYPE_USER,targetId);
    }

    @GetMapping("/followers/{userId}")
    public String getFollowers(@PathVariable("userId")int userId, Page page,Model model){
        User user = userService.findUserById(userId);
        if(user == null){
            throw new RuntimeException("用户不存在");
        }
        model.addAttribute("user",user);

        page.setLimit(5);
        page.setRows((int) followService.findEntityFollowerCount(EntityConstant.ENTITY_TYPE_USER,userId));
        page.setPath("/followers/" + userId);

        Set<Integer> targetIds = followService.findUserFollowers(userId, page.getOffset(), page.getLimit());
        List<FollowVo> followVos = new ArrayList<>();
        if(targetIds != null){
            for(int targetId : targetIds){
                FollowVo vo = new FollowVo();
                User u = userService.findUserById(targetId);
                Double followTime = redisTemplate.opsForZSet().score(
                        RedisKeyUtils.getFollowerKey(EntityConstant.ENTITY_TYPE_USER,userId), targetId);
                //当我当前登录用户而言---》是否关注他粉丝的状态
                boolean followStatus = hasFollowed(targetId);
                vo.setUserId(u.getId());
                vo.setUsername(u.getUsername());
                vo.setHeaderUrl(u.getHeaderUrl());
                vo.setFollowTime(new Date(followTime.longValue()));
                vo.setFollowStatus(followStatus);

                followVos.add(vo);
            }
        }
        model.addAttribute("followVos",followVos);
        return "/site/follower";
    }



}
