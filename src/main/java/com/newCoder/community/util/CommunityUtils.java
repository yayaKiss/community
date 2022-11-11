package com.newCoder.community.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.util.UUID;

/**
 * @author lijie
 * @date 2022-11-10 21:56
 * @Desc
 */
public class CommunityUtils {

    public static String generateUUID(){
        return UUID.randomUUID().toString().replaceAll("-","");
    }

    public static String MD5(String key){
        if(StringUtils.isEmpty(key)){
            return null;
        }
        return DigestUtils.md5DigestAsHex(key.getBytes());
    }
}
