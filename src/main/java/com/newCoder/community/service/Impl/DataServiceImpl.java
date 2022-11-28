package com.newCoder.community.service.Impl;

import com.newCoder.community.service.DataService;
import com.newCoder.community.util.RedisKeyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author lijie
 * @date 2022-11-22 17:07
 * @Desc
 */
@Service
public class DataServiceImpl implements DataService {
    @Autowired
    private RedisTemplate redisTemplate;

    private SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

    /**
     * 统计每日uv
     */
    @Override
    public void recordUV(String ip) {
        String redisKey = RedisKeyUtils.getUVKey(df.format(new Date()));
        redisTemplate.opsForHyperLogLog().add(redisKey,ip);
    }

    /**
     * 统计区间uv
     */
    @Override
    public long recordUV(Date start, Date end) {
        if(start == null || end == null){
            throw new IllegalArgumentException("参数不能为空");
        }
        if(start.after(end)){
            throw new IllegalArgumentException("请输入正确的时间区间");
        }
        //整理范围日期内的key，进行合并
        List<String> list = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(start);
        while(!calendar.getTime().after(end)){
            String redisKey = RedisKeyUtils.getUVKey(df.format(calendar.getTime()));
            list.add(redisKey);
            calendar.add(Calendar.DATE,1);
        }

        //合并key
        String unionKey = RedisKeyUtils.getDAUKey(df.format(start), df.format(end));
        redisTemplate.opsForHyperLogLog().union(unionKey,list.toArray());

        return redisTemplate.opsForHyperLogLog().size(unionKey);
    }

    /**
     * 统计每日dau
     */
    @Override
    public void recordDAU(int userId) {
        String redisKey = RedisKeyUtils.getDAUKey(df.format(new Date()));
        redisTemplate.opsForValue().setBit(redisKey,userId,true);
    }

    /**
     * 统计区间dau
     */
    @Override
    public long recordDAU(Date start, Date end) {
        if(start == null || end == null){
            throw new IllegalArgumentException("参数不能为空");
        }
        if(start.after(end)){
            throw new IllegalArgumentException("请输入正确的时间区间");
        }

        List<byte[]> list = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(start);
        while (! calendar.getTime().after(end)){
            String redisKey = RedisKeyUtils.getDAUKey(df.format(calendar.getTime()));
            list.add(redisKey.getBytes());
            calendar.add(Calendar.DATE,1);
        }

        //进行OR运算
        Long count = (Long) redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                String unionKey = RedisKeyUtils.getDAUKey(df.format(start), df.format(end));
                connection.bitOp(RedisStringCommands.BitOperation.OR, unionKey.getBytes(), list.toArray(new byte[0][0]));

                return connection.bitCount(unionKey.getBytes());
            }
        });

        return count;
    }
}
