package com.newCoder.community.entity;

import lombok.Data;

import java.util.Date;

/**
 * @author lijie
 * @date 2022-11-14 19:22
 * @Desc
 */
@Data
public class Comment {
    private int id;
    private int userId;
    private int entityType;
    private int entityId;
    private int targetId;
    private String content;
    private int status;
    private Date createTime;
}
