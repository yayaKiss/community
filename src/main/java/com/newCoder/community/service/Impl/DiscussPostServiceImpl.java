package com.newCoder.community.service.Impl;

import com.newCoder.community.dao.DiscussPostMapper;
import com.newCoder.community.entity.DiscussPost;
import com.newCoder.community.service.DiscussPostService;
import com.newCoder.community.util.SensitiveWordFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.util.HtmlUtils;

import java.util.Date;
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
    @Autowired
    private SensitiveWordFilter sensitiveWordFilter;

    public List<DiscussPost> findDiscussPosts(int userId, int offset, int limit){
        return discussPostMapper.selectDiscussPosts(userId, offset, limit);
    }

    public int findDiscussPostRows(int userId){
        return discussPostMapper.selectDiscussPostRows(userId);
    }

    @Override
    public int publish(int uid,String title,String content) {
        DiscussPost post = new DiscussPost();
        //字符转义
        title = HtmlUtils.htmlEscape(title);
        content = HtmlUtils.htmlEscape(content);

        //设置信息，敏感词过滤
        post.setUserId(uid);
        post.setTitle(sensitiveWordFilter.filter(title));
        post.setContent(sensitiveWordFilter.filter(content));
        post.setCreateTime(new Date());
        return discussPostMapper.insertDiscussPost(post);
    }

    @Override
    public DiscussPost findDiscussPostDetail(int postId) {
        return discussPostMapper.selectDiscussPostDetail(postId);
    }


}
