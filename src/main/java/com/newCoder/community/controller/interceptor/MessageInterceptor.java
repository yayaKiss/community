package com.newCoder.community.controller.interceptor;

import com.newCoder.community.entity.User;
import com.newCoder.community.service.MessageService;
import com.newCoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author lijie
 * @date 2022-11-19 17:41
 * @Desc
 */
@Component
public class MessageInterceptor implements HandlerInterceptor {
    @Autowired
    private MessageService messageService;
    @Autowired
    private HostHolder hostHolder;

    //controller后，模板引擎前
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        User user = hostHolder.getValue();
        if(user != null && modelAndView != null){
            int letterCount = messageService.findUnreadLetterCount(user.getId(), null);
            int noticeCount = messageService.findTopicUnreadCount(user.getId(), null);
            modelAndView.addObject("allUnreadCount",letterCount + noticeCount);
        }
    }
}
