package com.newCoder.community.entity;

import java.util.HashMap;
import java.util.Map;

/**
 * @author lijie
 * @date 2022-11-18 22:48
 * @Desc
 */
public class Event {
    //当前用户
    private int userId;
    //（点赞、评论、关注）的实体类型
    private int entityType;
    //实体id
    private int entityId;
    //实体对应的用户
    private int entityUserId;
    //主题 ---> 存放message中的conversationId，标识当前用户对实体做出的行为
    private String topic;
    //其他数据放到data
    private Map<String,Object> data = new HashMap<>();

    public int getUserId() {
        return userId;
    }

    public Event setUserId(int userId) {
        this.userId = userId;
        return this;
    }

    public int getEntityType() {
        return entityType;
    }

    public Event setEntityType(int entityType) {
        this.entityType = entityType;
        return this;
    }

    public int getEntityId() {
        return entityId;
    }

    public Event setEntityId(int entityId) {
        this.entityId = entityId;
        return this;
    }

    public int getEntityUserId() {
        return entityUserId;
    }

    public Event setEntityUserId(int entityUserId) {
        this.entityUserId = entityUserId;
        return this;
    }

    public String getTopic() {
        return topic;
    }

    public Event setTopic(String topic) {
        this.topic = topic;
        return this;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public Event setData(String key,Object value) {
        this.data.put(key,value);
        return this;
    }

    @Override
    public String toString() {
        return "Event{" +
                "userId=" + userId +
                ", entityType=" + entityType +
                ", entityId=" + entityId +
                ", entityUserId=" + entityUserId +
                ", data=" + data +
                '}';
    }
}
