package com.newCoder.community.service;

import com.newCoder.community.entity.Comment;

import java.util.List;

/**
 * @author lijie
 * @date 2022-11-14 19:47
 * @Desc
 */
public interface CommentService {
    List<Comment> getCommentsByEntity(int entityType,int entityId,int offset,int limit);

    int getCommentsCount(int entityType,int entityId);

    int addComment(Comment comment);
}
