package com.newCoder.community.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.newCoder.community.annotation.LoginRequired;
import com.newCoder.community.constant.LetterStatus;
import com.newCoder.community.constant.TopicConstant;
import com.newCoder.community.entity.Message;
import com.newCoder.community.entity.Page;
import com.newCoder.community.entity.User;
import com.newCoder.community.service.MessageService;
import com.newCoder.community.service.UserService;
import com.newCoder.community.util.HostHolder;
import com.newCoder.community.util.JsonResult;
import com.newCoder.community.vo.LetterVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.HtmlUtils;

import java.util.*;

/**
 * @author lijie
 * @date 2022-11-15 11:57
 * @Desc
 */
@Controller
@RequestMapping("/message")
public class MessageController {
    @Autowired
    private MessageService messageService;
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private UserService userService;

    @GetMapping("/letter.html")
    @LoginRequired
    public String letter(Model model, Page page){
        User user = hostHolder.getValue();
        int userId = user.getId();

        //设置分页信息
        page.setRows(messageService.findConversationCount(userId));
        page.setPath("/message/letter.html");
        page.setLimit(5);

        //当前页的会话
        List<Message> messages = messageService.findConversations(userId, page.getOffset(), page.getLimit());
        List<Map<String,Object>> messageVoList = new ArrayList<>();
        if(messages != null){
            for(Message message : messages){
                HashMap<String, Object> messageVo = new HashMap<>();
                //每一个会话信息
                messageVo.put("message",message);
                //每一个会话信息未读数量
                messageVo.put("unreadCount",messageService.findUnreadLetterCount(userId,message.getConversationId()));
                //消息私信数量（已读和未读）
                messageVo.put("letterCount",messageService.findLetterCount(message.getConversationId()));
                //每一个消息 对面user
                user = getTarget(message.getConversationId());
                messageVo.put("user",user);

                messageVoList.add(messageVo);
            }
        }
        model.addAttribute("messageVoList",messageVoList);

        //私信未读数量
        int letterUnreadCount = messageService.findUnreadLetterCount(userId,null);
        model.addAttribute("letterUnreadCount",letterUnreadCount);
        //系统通知未读总数量
        int noticeUnreadCount = messageService.findTopicUnreadCount(userId, null);
        model.addAttribute("noticeUnreadCount",noticeUnreadCount);

        return "/site/letter";
    }


    @GetMapping("/letter/detail/{conversionId}")
    @LoginRequired
    public String letterDetail(@PathVariable("conversionId") String conversionId,Model model,Page page){
        page.setLimit(5);
        page.setPath("/message/letter/detail/" + conversionId);
        page.setRows(messageService.findLetterCount(conversionId));

        //封装返回到detail页面的数据
        List<Message> letters = messageService.findLetters(conversionId,page.getOffset(),page.getLimit());
        List<LetterVo> letterVoList = new ArrayList<>();
        if(letters != null){
            for(Message letter : letters){
                LetterVo vo = new LetterVo();
                User user = userService.findUserById(letter.getFromId());
                vo.setHeaderUrl(user.getHeaderUrl());
                vo.setUsername(user.getUsername());
                vo.setCreateTime(letter.getCreateTime());
                vo.setContent(letter.getContent());
                vo.setId(letter.getId());
                letterVoList.add(vo);
            }
        }
        model.addAttribute("target",getTarget(conversionId));
        model.addAttribute("letterVoList",letterVoList);

        //将用户所有未读信息改为已读
        List<Integer> ids = getLetterIds(letters);
        if(!ids.isEmpty()){
            messageService.updateStatus(ids, LetterStatus.READ);
        }

        return "/site/letter-detail";
    }

    private List<Integer> getLetterIds(List<Message> letters) {
        List<Integer> ids = new ArrayList<>();
        for(Message letter : letters){
            if(letter.getFromId() != hostHolder.getValue().getId() && letter.getStatus() == 0){
                ids.add(letter.getId());
            }
        }
        return ids;
    }

    private User getTarget(String conversionId){
        String[] ids = conversionId.split("_");
        Integer id0 = Integer.parseInt(ids[0]);
        Integer id1 = Integer.parseInt(ids[1]);
        return id0 == hostHolder.getValue().getId() ? userService.findUserById(id1) : userService.findUserById(id0);
    }

    @LoginRequired
    @PostMapping("/letter/send")
    @ResponseBody
    public JsonResult sendMessage(String toName, String content){
        User user = userService.findUserByUserName(toName);
        if(user == null){
            return JsonResult.error(404,"发送失败，用户不存在");
        }

        //添加信息，状态是未读
        Message message = new Message();
        message.setFromId(hostHolder.getValue().getId());
        message.setToId(user.getId());
        String conversationId = message.getFromId() < message.getToId() ?
                message.getFromId() + "_" + message.getToId() : message.getToId() + "_" + message.getFromId();
        message.setConversationId(conversationId);
        message.setContent(content);
        message.setCreateTime(new Date());

        messageService.addMessage(message);

        return JsonResult.ok("发送成功");
    }

    @LoginRequired
    @GetMapping("/letter/delete/{id}")
    @ResponseBody
    public JsonResult deleteMessage(@PathVariable("id") int id){
        messageService.deleteMessage(id);
        return JsonResult.ok();
    }

    @GetMapping("/notice/list")
    public String noticeList(Model model){
        User user = hostHolder.getValue();
        if(user == null){
            throw new RuntimeException("用户不存在");
        }
        //查询评论通知
       Map<String,Object> messageVo = topicMessageVo(user.getId(), TopicConstant.COMMENT_TOPIC);
        model.addAttribute("commentVo",messageVo);

        //查询点赞通知
        messageVo = topicMessageVo(user.getId(), TopicConstant.LIKE_TOPIC);
        model.addAttribute("likeVo",messageVo);

        //查询关注通知
        messageVo = topicMessageVo(user.getId(), TopicConstant.FOLLOW_TOPIC);
        model.addAttribute("followVo",messageVo);

        //私信未读总数量
        int letterUnreadCount = messageService.findUnreadLetterCount(user.getId(), null);
        model.addAttribute("letterUnreadCount",letterUnreadCount);
        //系统通知未读总数量
        int noticeUnreadCount = messageService.findTopicUnreadCount(user.getId(), null);
        model.addAttribute("noticeUnreadCount",noticeUnreadCount);

        return "/site/notice";
    }

    private Map<String,Object> topicMessageVo(int userId,String topic){
        Map<String,Object> messageVo = new HashMap<>();
        //最新一条主题
        Message message = messageService.findLastedTopic(userId, topic);
        if(message == null){
            return null;
        }
        messageVo.put("createTime",message.getCreateTime());
        String content = HtmlUtils.htmlUnescape(message.getContent());
        Map<String,Object> map = JSONObject.parseObject(content);
        if(map != null){
            //来自某个用户的评论
            User user = userService.findUserById((Integer) map.get("userId"));
            messageVo.put("user",user);
            messageVo.put("entityId",map.get("entityId"));
            messageVo.put("entityType",map.get("entityType"));
            messageVo.put("postId",map.get("postId"));
        }
        //未读数量
        int unread = messageService.findTopicUnreadCount(userId, topic);
        messageVo.put("unread",unread);
        //多少条会话
        int count = messageService.findTopicCount(userId,topic);
        messageVo.put(topic + "Count",count);

        return messageVo;
    }

    @GetMapping("/notice/detail/{topic}")
    public String noticeDetail(@PathVariable("topic")String topic ,Page page,Model model){
        User user = hostHolder.getValue();
        //查询主题数量
        int count = messageService.findTopicCount(user.getId(), topic);

        //分页设置
        page.setRows(count);
        page.setPath("/message/notice/detail/" + topic);
        page.setLimit(5);

        //查询某个主题所有的message
        List<Message> notices = messageService.findTopicMessages(user.getId(), topic,page.getOffset(),page.getLimit());
        List<Map<String,Object>> noticeVos = new ArrayList<>();
        if(!notices.isEmpty()){
            for(Message notice : notices){
                Map<String,Object> map = new HashMap<>();
                //内容
                String content = HtmlUtils.htmlUnescape(notice.getContent());
                Map<String,Object> data = JSONObject.parseObject(content);
                //通知 ---》 时间，之后可能需要其他信息（便于扩展）
                map.put("notice",notice);
                //某个用户
                map.put("user",userService.findUserById((Integer) data.get("userId")));
                map.put("entityId",data.get("entityId"));
                map.put("entityType",data.get("entityType"));
                map.put("postId",data.get("postId"));
                //系统用户
                map.put("fromUser",userService.findUserById(notice.getFromId()));

                noticeVos.add(map);
            }
        }
        model.addAttribute("noticeVos",noticeVos);

        //修改为已读
        List<Integer> ids = getLetterIds(notices);
        if(!ids.isEmpty()){
            messageService.updateStatus(ids,LetterStatus.READ);
        }

        return "/site/notice-detail";
    }

}
