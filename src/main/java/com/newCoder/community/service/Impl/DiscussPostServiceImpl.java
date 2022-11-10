package com.newCoder.community.service.Impl;

import com.newCoder.community.dao.DiscussPostMapper;
import com.newCoder.community.entity.DiscussPost;
import com.newCoder.community.service.DiscussPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author lijie
 * @date 2022-11-09 16:16
 * @Desc
 */
@Service
public class DiscussPostServiceImpl implements DiscussPostService {
    @Autowired
    private DiscussPostMapper discussPostMapper;

    public List<DiscussPost> findDiscussPosts(int userId, int offset, int limit){
        return discussPostMapper.selectDiscussPosts(userId, offset, limit);
    }

    public int findDiscussPostRows(int userId){
        return discussPostMapper.selectDiscussPostRows(userId);
    }
}
