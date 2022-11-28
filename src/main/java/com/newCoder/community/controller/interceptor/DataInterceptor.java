package com.newCoder.community.controller.interceptor;

import com.newCoder.community.entity.User;
import com.newCoder.community.service.DataService;
import com.newCoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author lijie
 * @date 2022-11-22 17:39
 * @Desc
 */
@Component
public class DataInterceptor implements HandlerInterceptor {
    @Autowired
    HostHolder hostHolder;
    @Autowired
    DataService dateService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //统计UV
        String ip = request.getRemoteHost();
        dateService.recordUV(ip);

        //统计DAU
        User user = hostHolder.getValue();
        if(user != null){
            dateService.recordDAU(user.getId());
        }
        return true;
    }
}
