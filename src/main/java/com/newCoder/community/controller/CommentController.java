package com.newCoder.community.controller;

import com.newCoder.community.annotation.LoginRequired;
import com.newCoder.community.entity.Comment;
import com.newCoder.community.entity.User;
import com.newCoder.community.service.CommentService;
import com.newCoder.community.util.HostHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

    @PostMapping("/add/{postId}")
    @LoginRequired
    public String addComment(@PathVariable("postId") int postId, Comment comment){
        User user = hostHolder.getValue();
        comment.setUserId(user.getId());
        commentService.addComment(comment);
        return "redirect:/discuss/detail/" + postId;
    }
}
