package com.newCoder.community.dao;

import com.newCoder.community.entity.Comment;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * @author lijie
 * @date 2022-11-14 19:38
 * @Desc
 */
@SpringBootTest
public class CommentDaoTest {
    @Autowired
    private CommentMapper commentMapper;

    @Test
    public void selectTest(){
        int rows = commentMapper.selectCommentsCount(1, 228);
        System.out.println(rows);

        List<Comment> comments = commentMapper.selectCommentsByEntity(1, 228, 0, 5);
        for(Comment comment : comments){
            System.out.println(comment);
        }
    }
}
