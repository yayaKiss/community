package com.newCoder.community.dao;

import com.newCoder.community.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author lijie
 * @date 2022-11-14 19:24
 * @Desc
 */
@Mapper
public interface CommentMapper {

    List<Comment> selectCommentsByEntity(int entityType, int entityId, int offset, int limit);

    int selectCommentsCount(int entityType, int entityId );

    int insertComment(Comment comment);


}
