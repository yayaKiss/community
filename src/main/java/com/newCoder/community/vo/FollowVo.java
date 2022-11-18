package com.newCoder.community.vo;

import lombok.Data;

import java.util.Date;

/**
 * @author lijie
 * @date 2022-11-17 19:24
 * @Desc
 */
@Data
public class FollowVo {
    private int userId;
    private String username;
    private String headerUrl;
    private Date followTime;
    private boolean followStatus;
}
