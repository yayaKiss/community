package com.newCoder.community.dao;

import com.newCoder.community.entity.DiscussPost;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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
    @Test
    public void selectDiscussPost(){
        List<DiscussPost> discussPosts = discussPostMapper.selectDiscussPosts(4,0,10);
        for(DiscussPost discussPost : discussPosts){
            System.out.println(discussPost);
        }

        int row = discussPostMapper.selectDiscussPostRows(4);
        System.out.println(row);
    }

}
