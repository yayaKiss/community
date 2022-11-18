package com.newCoder.community.vo;

import lombok.Data;

import java.util.Date;

/**
 * @author lijie
 * @date 2022-11-17 22:20
 * @Desc
 */
@Data
public class PostVo {
    //帖子id
    private int id;
    //帖子标题
    private String title;
    //帖子内容
    private String content;
    //帖子发布时间
    private Date publishTime;
    //点赞数量
    private long likeCount;
}
