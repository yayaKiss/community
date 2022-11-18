package com.newCoder.community.util;

/**
 * @author lijie
 * @date 2022-11-16 20:55
 * @Desc
 */
public class RedisKeyUtils {
    private static final String SPLIT = ":";
    private static final String PREFIX_ENTITY_LIKE= "like:entity";
    private static final String PREFIX_USER_LIKE = "like:user";
    private static final String PREFIX_FOLLOWEE = "followee";
    private static final String PREFIX_FOLLOWER = "follower";
    private static final String PREFIX_KAPTCHA = "kaptcha";
    private static final String PREFIX_TICKET = "ticket";
    private static final String PREFIX_USER = "user";
    private static final String PREFIX_FORGET_CODE = "user:code:";

    //获取实体的赞
    //like:entity:{entityType}:{entityId} ---> value:set集合（存放点赞的用户id）
    public static String getEntityLikeKey(int entityType,int entityId){
        return PREFIX_ENTITY_LIKE + SPLIT + entityType + SPLIT + entityId;
    }

    //用户的赞
    public static String getUserLikeKey(int userId){
        return PREFIX_USER_LIKE + SPLIT + userId;
    }

    //关注实体(当前用户关注第三方用户，以当前用户id + 第三方类型 为键)
    //followee:userId:entityType ---> zset(关注的entityId)
    public static String getFolloweeKey(int userId,int entityType){
        return PREFIX_FOLLOWEE + SPLIT + userId +SPLIT + entityType;
    }

    //粉丝(当前用户关注后，第三方用户为 类型 + id 为键，存储粉丝) --->(粉丝的id，当前userId)
    //followee:entityType:entityId ---> zset(当前用户userId)
    public static String getFollowerKey(int entityType,int entityId){
        return PREFIX_FOLLOWER + SPLIT + entityType + SPLIT + entityId;
    }

    //验证码
    public static String getKaptchaKey(String owner){
        return PREFIX_KAPTCHA + SPLIT + owner;
    }

    //登录凭证
    public static String getTicketKey(String ticket){
        return PREFIX_TICKET + SPLIT +ticket;
    }

    //用户信息
    public static String getUserKey(int userId){
        return PREFIX_USER + SPLIT + userId;
    }

    //邮箱验证码
    public static String getEmailCode(String email){
        return PREFIX_FORGET_CODE + SPLIT + email;
    }
}
