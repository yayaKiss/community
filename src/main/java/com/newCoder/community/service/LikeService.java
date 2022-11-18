package com.newCoder.community.service;

/**
 * @author lijie
 * @date 2022-11-16 21:01
 * @Desc
 */
public interface LikeService {

    //获取实体的点赞数
    long findEntityLikeCount(int entityType,int entityId);

    //用户对实体点赞
    void like(int userId,int entityType,int entityId,int entityUserId);

    //获取某个用户对实体点赞的状态
    int findEntityLikeStatus(int userId,int entityType,int entityId);

    //获取用户的点赞数
    int findUserLikeCount(int userId);
}
