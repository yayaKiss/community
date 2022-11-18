package com.newCoder.community.service.Impl;

import com.newCoder.community.service.LikeService;
import com.newCoder.community.util.RedisKeyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

/**
 * @author lijie
 * @date 2022-11-16 21:09
 * @Desc
 */
@Service
public class LikeServiceImpl implements LikeService {
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public long findEntityLikeCount(int entityType, int entityId) {
        String key = RedisKeyUtils.getEntityLikeKey(entityType,entityId);
        return redisTemplate.opsForSet().size(key);

    }

    @Override
    public void like(int userId, int entityType, int entityId,int entityUserId) {
//        String key = RedisKeyUtils.getEntityLikeKey(entityType,entityId);
//        boolean isExist = redisTemplate.opsForSet().isMember(key, userId);
//        if(isExist){
//            redisTemplate.opsForSet().remove(key,userId);
//        }else{
//            redisTemplate.opsForSet().add(key,userId);
//        }
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String entityLikeKey = RedisKeyUtils.getEntityLikeKey(entityType,entityId);
                String entityUserLikeKey = RedisKeyUtils.getUserLikeKey(entityUserId);
                //是否对该实体（帖子或评论）点赞
                boolean isMember = operations.opsForSet().isMember(entityLikeKey, userId);
                //开启事务
                operations.multi();
                if(isMember){
                    //实体赞删除
                    operations.opsForSet().remove(entityLikeKey,userId);
                    //用户赞减一
                    operations.opsForValue().decrement(entityUserLikeKey);
                }else{
                    operations.opsForSet().add(entityLikeKey,userId);
                    operations.opsForValue().increment(entityUserLikeKey);
                }
                //提交事务
                return  operations.exec();
            }
        });

    }

    @Override
    public int findEntityLikeStatus(int userId, int entityType, int entityId) {
        String key = RedisKeyUtils.getEntityLikeKey(entityType,entityId);
        return redisTemplate.opsForSet().isMember(key,userId) ? 1 : 0;
    }

    @Override
    public int findUserLikeCount(int userId) {
        String userLikeKey = RedisKeyUtils.getUserLikeKey(userId);
        Integer userLikeCount  = (Integer) redisTemplate.opsForValue().get(userLikeKey);
        return userLikeCount == null ? 0 : userLikeCount;
    }
}
