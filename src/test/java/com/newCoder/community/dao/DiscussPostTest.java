package com.newCoder.community.dao;

import com.newCoder.community.entity.DiscussPost;
import com.newCoder.community.util.SensitiveWordFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;
import java.util.List;

/**
 * @author lijie
 * @date 2022-11-09 15:08
 * @Desc
 */
@SpringBootTest
public class DiscussPostTest {
    @Autowired
    private DiscussPostMapper discussPostMapper;
    @Autowired
    private SensitiveWordFilter sensitiveWordFilter;
    @Test
    public void selectDiscussPost(){
        List<DiscussPost> discussPosts = discussPostMapper.selectDiscussPosts(4,0,10);
        for(DiscussPost discussPost : discussPosts){
            System.out.println(discussPost);
        }

        int row = discussPostMapper.selectDiscussPostRows(4);
        System.out.println(row);
    }

    @Test
    public void insertPost(){
        DiscussPost post = new DiscussPost();
        post.setUserId(149);
        post.setTitle(sensitiveWordFilter.filter("xxx赌博，快来人"));
        post.setContent(sensitiveWordFilter.filter("来到这个地方，可以赌博，可以开票，加QQ:123456"));
        post.setCreateTime(new Date());

        discussPostMapper.insertDiscussPost(post);
        System.out.println(post.getId());
    }

    @Test
    public void selectDiscussPostDetail(){
        DiscussPost post = discussPostMapper.selectDiscussPostDetail(283);
        System.out.println(post);
    }

}
