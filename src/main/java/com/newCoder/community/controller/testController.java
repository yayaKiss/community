package com.newCoder.community.controller;

import com.alibaba.fastjson.JSONObject;

import com.newCoder.community.dao.UserMapper;
import com.newCoder.community.entity.Page;
import com.newCoder.community.entity.User;
import com.newCoder.community.util.CommunityUtils;
import com.newCoder.community.util.JsonResult;
import com.newCoder.community.vo.LoginVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.*;

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

    @PostMapping("/getJson")
    @ResponseBody
    public JsonResult getJson(String name,String age){
        System.out.println(name);
        System.out.println(age);
        return JsonResult.ok();
    }

    @PostMapping("/getJsonString")
    @ResponseBody
    public String getJsonString(String name,String age){
        System.out.println(name);
        System.out.println(age);
        return JSONObject.toJSONString(JsonResult.ok());
    }

    @GetMapping("/getUserList")
    public String getUserList(Model model, Page page){
        List<User> userList = new ArrayList<>();
        for(int i = 0;i < 5;i++){
            User user = userMapper.selectById(111 + i);
            userList.add(user);
        }
        model.addAttribute("userList",userList);
        return "/test";
    }

}
