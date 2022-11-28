package com.newCoder.community.service;


import com.newCoder.community.entity.DiscussPost;

import java.util.List;

/**
 * @author lijie
 * @date 2022-11-09 15:48
 * @Desc
 */
public interface DiscussPostService {

    List<DiscussPost> findDiscussPosts(int userId,int offset,int limit,int orderMode);

    int findDiscussPostRows(int userId);

    int publish(DiscussPost post);

    DiscussPost findDiscussPostDetail(int postId);

    //置顶 | 取消置顶
    int topPost(int id,int type);

    //加精 | 取消加精
    int wonderfulPost(int id,int status);

    //删除
    int deletePost(int id);

    //修改分数
    int updateScore(int id,double score);

}
