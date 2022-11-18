package com.newCoder.community.service.Impl;

import com.newCoder.community.constant.EntityConstant;
import com.newCoder.community.dao.CommentMapper;
import com.newCoder.community.dao.DiscussPostMapper;
import com.newCoder.community.entity.Comment;
import com.newCoder.community.entity.DiscussPost;
import com.newCoder.community.service.CommentService;
import com.newCoder.community.util.SensitiveWordFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import java.util.Date;
import java.util.List;

/**
 * @author lijie
 * @date 2022-11-14 19:49
 * @Desc
 */
@Service
public class CommentServiceImpl implements CommentService {
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    SensitiveWordFilter sensitiveWordFilter;
    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Override
    public List<Comment> findCommentsByEntity(int entityType, int entityId, int offset, int limit) {
        return commentMapper.selectCommentsByEntity(entityType,entityId,offset,limit);
    }

    @Override
    public int findCommentsCount(int entityType, int entityId) {
        return commentMapper.selectCommentsCount(entityType,entityId);
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED,propagation = Propagation.REQUIRED)
    public int addComment(Comment comment) {
        if(comment == null){
            throw new IllegalArgumentException("参数不能为空");
        }
        //插入评论
        comment.setContent(HtmlUtils.htmlEscape(comment.getContent()));
        comment.setContent(sensitiveWordFilter.filter(comment.getContent()));
        comment.setCreateTime(new Date());
        int rows = commentMapper.insertComment(comment);

        //修改帖子评论数量
        if(comment.getEntityType() == EntityConstant.ENTITY_TYPE_COMMENT){
            int count = commentMapper.selectCommentsCount(comment.getEntityType(),comment.getEntityId());
            discussPostMapper.updatePostCommentCount(comment.getEntityId(),count);
        }

        return rows;
    }

    @Override
    public int findCommentPostCount(int userId) {
        return commentMapper.selectCommentPostCount(userId,EntityConstant.ENTITY_TYPE_POST);
    }

    @Override
    public List<Comment> findCommentPosts(int userId,int offset,int limit) {
        return commentMapper.selectCommentPosts(userId,offset,limit);
    }

    @Override
    public Comment findCommentById(int id) {
        return commentMapper.selectCommentById(id);
    }

}
