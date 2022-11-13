package com.newCoder.community.vo;

import lombok.Data;

/**
 * @author lijie
 * @date 2022-11-12 23:08
 * @Desc
 */
@Data
public class UpdateCodeVo {
    private String oldPassword;
    private String newPassword;
    private String confirmPassword;
}
