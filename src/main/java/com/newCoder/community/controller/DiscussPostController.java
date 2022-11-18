package com.newCoder.community.controller;

import com.newCoder.community.constant.EntityConstant;
import com.newCoder.community.entity.Comment;
import com.newCoder.community.entity.DiscussPost;
import com.newCoder.community.entity.Page;
import com.newCoder.community.entity.User;
import com.newCoder.community.service.CommentService;
import com.newCoder.community.service.DiscussPostService;
import com.newCoder.community.service.LikeService;
import com.newCoder.community.service.UserService;
import com.newCoder.community.util.HostHolder;
import com.newCoder.community.util.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;


/**
 * @author lijie
 * @date 2022-11-14 11:22
 * @Desc
 */
@Controller
@RequestMapping("/discuss")
public class DiscussPostController {
    @Autowired
    private DiscussPostService discussPostService;
    @Autowired
    private UserService userService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private LikeService likeService;

    @PostMapping("/publish")
    @ResponseBody
    public JsonResult publishPost(String title,String content){
        User user = hostHolder.getValue();
        if(user == null){
            return JsonResult.error(403,"您还没有登录");
        }
        discussPostService.publish(user.getId(),title,content);
        return JsonResult.ok("发布成功");

    }

    @GetMapping("/detail/{postId}")
    public String postDetail(@PathVariable("postId") int postId, Model model, Page page){
        //帖子
        DiscussPost post = discussPostService.findDiscussPostDetail(postId);
        //点赞
        long likeCount = likeService.findEntityLikeCount(EntityConstant.ENTITY_TYPE_POST, postId);
        int likeStatus = hostHolder.getValue() == null ? 0 :
                likeService.findEntityLikeStatus(hostHolder.getValue().getId(),EntityConstant.ENTITY_TYPE_POST,postId);
        //发帖人
        User user = userService.findUserById(post.getUserId());
        model.addAttribute("likeCount",likeCount);
        model.addAttribute("likeStatus",likeStatus);
        model.addAttribute("post",post);
        model.addAttribute("user",user);
        //评论分页设置
        page.setLimit(5);
        page.setPath("/discuss/detail/" + postId);
        page.setRows(commentService.findCommentsCount(EntityConstant.ENTITY_TYPE_POST,postId));

        List<Comment> commentList = commentService.findCommentsByEntity(EntityConstant.ENTITY_TYPE_POST, postId, page.getOffset(), page.getLimit());
        List<Map<String,Object>> commentVoList = new ArrayList<>();
        if(commentList != null){
            //遍历每一条评论,加入到map中，同时查出user，需要用户的头像信息
            for(Comment comment : commentList){
                HashMap<String, Object> commentVo = new HashMap<>();
                //评论
                commentVo.put("comment",comment);
                //作者(用到头像)
                commentVo.put("user",userService.findUserById(comment.getUserId()));
                //点赞
                likeCount = likeService.findEntityLikeCount(EntityConstant.ENTITY_TYPE_COMMENT,comment.getId());
                likeStatus = hostHolder.getValue() == null ? 0 :
                        likeService.findEntityLikeStatus(hostHolder.getValue().getId(),EntityConstant.ENTITY_TYPE_COMMENT,comment.getId());
                commentVo.put("likeCount",likeCount);
                commentVo.put("likeStatus",likeStatus);
                //查出此条评论的所有回复
                List<Comment> replyList = commentService.findCommentsByEntity(EntityConstant.ENTITY_TYPE_COMMENT, comment.getId(),0,Integer.MAX_VALUE);
                List<Map<String,Object>> replyListVo = new ArrayList<>();
                if(replyList != null){
                    for(Comment reply : replyList){
                        Map<String,Object> replyVo = new HashMap<>();
                        //回复
                        replyVo.put("reply",reply);
                        //作者
                        replyVo.put("user",userService.findUserById(reply.getUserId()));
                        //点赞
                        likeCount = likeService.findEntityLikeCount(EntityConstant.ENTITY_TYPE_COMMENT,reply.getId());
                        likeStatus = hostHolder.getValue() == null ? 0 :
                                likeService.findEntityLikeStatus(hostHolder.getValue().getId(),EntityConstant.ENTITY_TYPE_COMMENT,reply.getId());
                        replyVo.put("likeCount",likeCount);
                        replyVo.put("likeStatus",likeStatus);
                        //回复对象
                        User target = reply.getTargetId() == 0 ? null : userService.findUserById(reply.getTargetId());
                        replyVo.put("target",target);

                        //添加到replyVoList中
                        replyListVo.add(replyVo);
                    }
                    //该条评论下的所有回复的数量
                    commentVo.put("replyCount",commentService.findCommentsCount(EntityConstant.ENTITY_TYPE_COMMENT,comment.getId()));

                    //此条评论的所有回复添加的commentVo中
                    commentVo.put("replyList",replyListVo);
                }
                commentVoList.add(commentVo);
            }
        }
        model.addAttribute("commentVoList",commentVoList);
        return "/site/discuss-detail";
    }

}
