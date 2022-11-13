package com.newCoder.community.vo;

import lombok.Data;

/**
 * @author lijie
 * @date 2022-11-11 19:53
 * @Desc
 */
@Data
public class LoginVo {
    private String username;
    private String password;
    private String code;
    private boolean rememberme;
}
