package com.newCoder.community.advice;

import com.alibaba.fastjson.JSON;
import com.newCoder.community.util.JsonResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author lijie
 * @date 2022-11-16 12:49
 * @Desc
 */
@Slf4j
@ControllerAdvice(annotations = Controller.class) //服务器controller表现层发生异常来到此处
public class ExceptionAdvice {

    @ExceptionHandler
    public void handException(Exception e, HttpServletRequest request, HttpServletResponse response) throws IOException {
        log.error("服务器发送异常" + e.getMessage());
        //把服务器栈的异常信息记录
        for(StackTraceElement element :  e.getStackTrace()){
            log.error(element.toString());
        }

        //记录完日志，给浏览器响应（可能返回错误页面，可能需要返回json字符串（发送异步请求过来的））
        String xRequestedWith = request.getHeader("x-requested-with");
        //异步请求
        if("XMLHttpRequest".equals(xRequestedWith)){
            response.setContentType("application/json;charset=utf-8");
            PrintWriter writer = response.getWriter();
            writer.write(JSON.toJSONString(JsonResult.error(500,"服务器异常")));
        }else{
            response.sendRedirect(request.getContextPath() + "/error");
        }
    }
}
