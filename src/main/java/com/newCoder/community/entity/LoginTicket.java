package com.newCoder.community.entity;

import lombok.Data;

import java.util.Date;

/**
 * @author lijie
 * @date 2022-11-12 09:19
 * @Desc
 */
@Data
public class LoginTicket {
    private int id;
    private int userId;
    private String ticket;
    private int status;
    private Date expired;
}
