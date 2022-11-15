package com.newCoder.community.entity;

import lombok.Data;

import java.util.Date;

/**
 * @author lijie
 * @date 2022-11-15 12:14
 * @Desc
 */
@Data
public class Message {
    private int id;
    private int fromId;
    private int toId;
    private String conversationId;
    private String content;
    private int status;
    private Date createTime;
}
