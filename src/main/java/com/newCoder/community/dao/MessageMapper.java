package com.newCoder.community.dao;

import com.newCoder.community.entity.Message;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author lijie
 * @date 2022-11-15 12:08
 * @Desc
 */
@Mapper
public interface MessageMapper {
    //查询当前用户的所有最新会话消息
    List<Message> selectConversations(int userId,int offset,int limit);

    //查询当前用户会话的总数量(分页用到)
    int selectConversationCount(int userId);

    //查询所有私信
    List<Message> selectLetters(String conversationId,int offset,int limit);

    //查询私信的总数量数量(分页用到总行数)
    int selectLetterCount(String conversationId);

    //查询未读信息
    int selectUnreadLetterCount(int userId,String conversationId);

    //添加私信
    int insertMessage(Message message);

    //更新私信的状态
    int updateStatus(List<Integer> ids,int status);

    //删除某条私信
    int deleteMessage(int id);

    //查询用户主题的最新一条消息
    Message selectLastedTopic(int userId,String topic);

    //查询用户主题的数量
    int selectTopicCount(int userId,String topic);

    //查询用户主题未读数量
    int selectTopicUnreadCount(int userId,String topic);

    //查询某个主题所有message详情
    List<Message> selectTopicMessages(int userId,String topic,int offset,int limit);
}
