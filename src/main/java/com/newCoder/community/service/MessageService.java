package com.newCoder.community.service;

import com.newCoder.community.entity.Message;

import java.util.List;

/**
 * @author lijie
 * @date 2022-11-15 13:36
 * @Desc
 */
public interface MessageService {
    //查询当前用户的所有最新会话消息
    List<Message> findConversations(int userId, int offset, int limit);

    //查询当前用户会话的总数量(分页用到)
    int findConversationCount(int userId);

    //查询所有私信
    List<Message> findLetters(String conversationId,int offset,int limit);

    //查询私信的总数量数量(分页用)
    int findLetterCount(String conversationId);

    //查询未读信息
    int findUnreadLetterCount(int userId,String conversationId);

    int addMessage(Message message);

    int updateStatus(List<Integer> ids,int status);

    int deleteMessage(int id);
}
