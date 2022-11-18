package com.newCoder.community.service.Impl;

import com.newCoder.community.constant.EntityConstant;
import com.newCoder.community.entity.User;
import com.newCoder.community.service.FollowService;
import com.newCoder.community.service.UserService;
import com.newCoder.community.util.RedisKeyUtils;
import com.newCoder.community.vo.FollowVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author lijie
 * @date 2022-11-17 12:34
 * @Desc
 */
@Service
public class FollowServiceImpl implements FollowService {
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public void follow(int userId, int entityType, int entityId) {
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                //判断有没有关注，从第三方用户中找是否有当前用户
                String entityFollowerKey = RedisKeyUtils.getFollowerKey(entityType,entityId);
                String entityFolloweeKey = RedisKeyUtils.getFolloweeKey(userId,entityType);
                Double score = operations.opsForZSet().score(entityFollowerKey, userId);
                operations.multi();
                //没有关注，点击进行关注
                if(score == null){
                    operations.opsForZSet().add(entityFolloweeKey,entityId,System.currentTimeMillis());
                    operations.opsForZSet().add(entityFollowerKey,userId,System.currentTimeMillis());
                }else{ //已关注，再次点击就不关注，删掉
                    operations.opsForZSet().remove(entityFolloweeKey,entityId);
                    operations.opsForZSet().remove(entityFollowerKey,userId);
                }
                return operations.exec();
            }
        });
    }

    @Override
    public long findEntityFolloweeCount(int userId,int entityType) {
        String entityFolloweeKey = RedisKeyUtils.getFolloweeKey(userId,entityType);
        return redisTemplate.opsForZSet().zCard(entityFolloweeKey);
    }

    @Override
    public long findEntityFollowerCount(int entityType,int entityId) {
        String entityFollowerKey = RedisKeyUtils.getFollowerKey(entityType,entityId);
        return redisTemplate.opsForZSet().zCard(entityFollowerKey);
    }

    @Override
    public boolean findUserFollowStatus(int userId, int entityType, int entityId) {
        String entityFolloweeKey = RedisKeyUtils.getFolloweeKey(userId,entityType);
        return redisTemplate.opsForZSet().score(entityFolloweeKey,entityId) != null;
    }

    @Override
    public Set<Integer> findUserFollowees(int userId, int offset, int limit) {
        String followeeKey = RedisKeyUtils.getFolloweeKey(userId, EntityConstant.ENTITY_TYPE_USER);
        return redisTemplate.opsForZSet().reverseRange(followeeKey, offset, limit + offset - 1);

    }

    @Override
    public Set<Integer> findUserFollowers(int userId, int offset, int limit) {
        String followerKey = RedisKeyUtils.getFollowerKey(EntityConstant.ENTITY_TYPE_USER,userId);
        return redisTemplate.opsForZSet().reverseRange(followerKey, offset, limit + offset - 1);
    }


}
