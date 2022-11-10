package com.newCoder.community.entity;

import lombok.Data;

import java.util.Date;

/**
 * @author lijie
 * @date 2022-11-09 14:34
 * @Desc
 */
@Data
public class DiscussPost {
    // id  user_id  title   content    type  status  create_time  comment_count   score
    private int id;
    private int userId;
    private String title;
    private String content;
    private int type;
    private int status;
    private Date createTime;
    private int commentCount;
    private double score;

}
