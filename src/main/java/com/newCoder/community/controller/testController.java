package com.newCoder.community.controller;

import com.alibaba.druid.support.json.JSONUtils;
import com.alibaba.fastjson.JSON;
import com.newCoder.community.dao.UserMapper;
import com.newCoder.community.entity.User;
import com.newCoder.community.util.JsonResult;
import com.newCoder.community.vo.LoginVo;
import com.sun.org.apache.xpath.internal.operations.Mod;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * @author lijie
 * @date 2022-11-11 20:36
 * @Desc
 */
@Slf4j
@Controller
public class testController {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private UserMapper userMapper;

    @GetMapping("/set")
    @ResponseBody
    public String setSession(HttpSession session){
        LoginVo user = new LoginVo();
        user.setUsername("123");
        user.setPassword("123");
        user.setCode("akd1");
        session.setAttribute("user",user);
        System.out.println(session.getId());
        return "set session";
    }

    @GetMapping("/get")
    @ResponseBody
    public String getSession(HttpServletRequest request){
        LoginVo user = (LoginVo)request.getSession().getAttribute("user");
        log.info("LoginVo:{}",user);
        return "get session";
    }

    @GetMapping("/test")
    public String test(){
        return "/test";
    }
    @PostMapping("/testR")
    public String testR(LoginVo vo, Model model){
        if(userMapper.selectByName(vo.getUsername()) == null){
            model.addAttribute("msg","用户名或密码错误");
            return "/test";
        }
        return "/index";
    }

}
