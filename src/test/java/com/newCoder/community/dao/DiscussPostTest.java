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
        List<DiscussPost> discussPosts = discussPostMapper.selectDiscussPosts(4,0,10,0);
        for(DiscussPost discussPost : discussPosts){
            System.out.println(discussPost);
        }

        int row = discussPostMapper.selectDiscussPostRows(4);
        System.out.println(row);
    }

    @Test
    public void insertPost(){
        for(int i = 0;i < 300000;i++){
            DiscussPost post = new DiscussPost();
            post.setUserId(11);
            post.setTitle(sensitiveWordFilter.filter("我是管理员"));
            post.setContent(sensitiveWordFilter.filter("我是管理员，你们都老实点"));
            post.setCreateTime(new Date());

            discussPostMapper.insertDiscussPost(post);
        }
    }

    @Test
    public void selectDiscussPostDetail(){
        DiscussPost post = discussPostMapper.selectDiscussPostDetail(283);
        System.out.println(post);
    }

}
