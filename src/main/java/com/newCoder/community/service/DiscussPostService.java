package com.newCoder.community.service;


import com.newCoder.community.entity.DiscussPost;

import java.util.List;

/**
 * @author lijie
 * @date 2022-11-09 15:48
 * @Desc
 */
public interface DiscussPostService {

    List<DiscussPost> findDiscussPosts(int userId,int offset,int limit);

    int findDiscussPostRows(int userId);

    int publish(int uid,String title,String content);

    DiscussPost findDiscussPostDetail(int postId);

}
