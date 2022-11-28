package com.newCoder.community.service.Impl;

import com.newCoder.community.dao.MessageMapper;
import com.newCoder.community.entity.Message;
import com.newCoder.community.service.MessageService;
import com.newCoder.community.util.SensitiveWordFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

/**
 * @author lijie
 * @date 2022-11-15 13:37
 * @Desc
 */
@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageMapper messageMapper;
    @Autowired
    private SensitiveWordFilter sensitiveWordFilter;

    @Override
    public List<Message> findConversations(int userId, int offset, int limit) {
        return messageMapper.selectConversations(userId,offset,limit);
    }

    @Override
    public int findConversationCount(int userId) {
        return messageMapper.selectConversationCount(userId);
    }

    @Override
    public List<Message> findLetters(String conversationId, int offset, int limit) {
        return messageMapper.selectLetters(conversationId,offset,limit);
    }

    @Override
    public int findLetterCount(String conversationId) {
        return messageMapper.selectLetterCount(conversationId);
    }

    @Override
    public int findUnreadLetterCount(int userId, String conversationId) {
        return messageMapper.selectUnreadLetterCount(userId,conversationId);
    }

    @Override
    public int addMessage(Message message) {
        message.setContent(HtmlUtils.htmlEscape(message.getContent()));
        message.setContent(sensitiveWordFilter.filter(message.getContent()));
        return messageMapper.insertMessage(message);
    }

    @Override
    public int updateStatus(List<Integer> ids,int status) {
        return messageMapper.updateStatus(ids,status);
    }

    @Override
    public int deleteMessage(int id) {
        return messageMapper.deleteMessage(id);
    }

    @Override
    public Message findLastedTopic(int userId, String topic) {
        return messageMapper.selectLastedTopic(userId,topic);
    }

    @Override
    public int findTopicCount(int userId, String topic) {
        return messageMapper.selectTopicCount(userId, topic);
    }

    @Override
    public int findTopicUnreadCount(int userId, String topic) {
        return messageMapper.selectTopicUnreadCount(userId,topic);
    }

    @Override
    public List<Message> findTopicMessages(int userId, String topic,int offset,int limit) {
        return messageMapper.selectTopicMessages(userId,topic, offset, limit);
    }
}
