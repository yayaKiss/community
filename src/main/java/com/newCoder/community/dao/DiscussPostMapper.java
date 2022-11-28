package com.newCoder.community.dao;

import com.newCoder.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author lijie
 * @date 2022-11-09 14:38
 * @Desc
 */
@Mapper
public interface DiscussPostMapper {

    List<DiscussPost> selectDiscussPosts(@Param("userId") int userId, @Param("offset") int offset, @Param("limit") int limit,int orderMode);

    int selectDiscussPostRows(@Param("userId") int userId);

    int insertDiscussPost(DiscussPost discussPost);

    DiscussPost selectDiscussPostDetail(@Param("postId") int id);

    int updatePostCommentCount(int entityId, int count);

    //置顶
    int updateType(int id,int type);

    //加精
    int updateStatus(int id,int status);

    //修改分数
    int updateScore(int id,double score);


}
