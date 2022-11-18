package com.newCoder.community.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

/**
 * @author lijie
 * @date 2022-11-16 17:52
 * @Desc
 */
@Configuration
public class RedisConfig {
    @Bean
    public RedisTemplate<String,Object> redisTemplate(RedisConnectionFactory factory){
        RedisTemplate<String,Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(factory);

        //配置key的序列化器
        redisTemplate.setKeySerializer(RedisSerializer.string());
        //配置value的序列化器
        redisTemplate.setValueSerializer(RedisSerializer.json());
        //配置hash的key的序列化器
        redisTemplate.setHashKeySerializer(RedisSerializer.string());
        //配置hash的value的序列化器
        redisTemplate.setHashValueSerializer(RedisSerializer.json());

        redisTemplate.afterPropertiesSet();
        return redisTemplate;

    }
}