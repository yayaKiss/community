package com.newCoder.community.vo;

import lombok.Data;

import java.util.Date;

/**
 * @author lijie
 * @date 2022-11-15 15:30
 * @Desc
 */
@Data
public class LetterVo {
    private int id;
    private String headerUrl;
    private String username;
    private String content;
    private Date createTime;
}
