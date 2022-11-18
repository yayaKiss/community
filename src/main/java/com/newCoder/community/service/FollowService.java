package com.newCoder.community.service;

import com.newCoder.community.entity.User;
import com.newCoder.community.vo.FollowVo;

import java.util.List;
import java.util.Set;

/**
 * @author lijie
 * @date 2022-11-17 12:23
 * @Desc
 */
public interface FollowService {

    //关注
    void follow(int userId,int entityType,int entityId);

    //查找实体的粉丝数量
    long findEntityFollowerCount(int entityType,int entityId);

    //查找用户的关注的实体的数量
    long findEntityFolloweeCount(int userId,int entityType);

    //查找当前用户对其他用户的关注状态
    boolean findUserFollowStatus(int userId,int entityType,int entityId);

    //查找当前用户关注的所有人
    Set<Integer> findUserFollowees(int userId, int offset, int limit);


//    查找当前用户的所有粉丝
    Set<Integer> findUserFollowers(int userId,int offset,int limit);
}
