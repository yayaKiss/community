package com.newCoder.community.service;

import com.newCoder.community.entity.Comment;
import com.newCoder.community.entity.DiscussPost;

import java.util.List;

/**
 * @author lijie
 * @date 2022-11-14 19:47
 * @Desc
 */
public interface CommentService {
    List<Comment> findCommentsByEntity(int entityType,int entityId,int offset,int limit);

    int findCommentsCount(int entityType,int entityId);

    int addComment(Comment comment);

    int findCommentPostCount(int userId);

    List<Comment> findCommentPosts(int userId,int offset,int limit);

    Comment findCommentById(int id);
}
