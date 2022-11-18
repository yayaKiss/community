package com.newCoder.community.vo;

import lombok.Data;

import java.util.Date;

/**
 * @author lijie
 * @date 2022-11-18 09:35
 * @Desc
 */
@Data
public class ReplyVo {
    private int postId;
    private String title;
    private String content;
    private Date publishTime;
}
