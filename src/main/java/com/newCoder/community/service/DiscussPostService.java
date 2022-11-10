package com.newCoder.community.service;

import com.newCoder.community.dao.DiscussPostMapper;
import com.newCoder.community.entity.DiscussPost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author lijie
 * @date 2022-11-09 15:48
 * @Desc
 */
public interface DiscussPostService {

    List<DiscussPost> findDiscussPosts(int userId,int offset,int limit);

    int findDiscussPostRows(int userId);
}
