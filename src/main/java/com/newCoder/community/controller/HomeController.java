package com.newCoder.community.controller;

import com.newCoder.community.entity.DiscussPost;
import com.newCoder.community.entity.Page;
import com.newCoder.community.entity.User;
import com.newCoder.community.service.DiscussPostService;
import com.newCoder.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author lijie
 * @date 2022-11-09 16:14
 * @Desc
 */
@Controller
public class HomeController {
    @Autowired
    private UserService userService;
    @Autowired
    private DiscussPostService discussPostService;

    @GetMapping("/index")
    //在springMVC中，参数都dispatchServlet自动进行实例化，并将page注入到了model中
    public String test(Model model, Page page){
        page.setRows(discussPostService.findDiscussPostRows(0));
        page.setPath("/index");

        List<DiscussPost> list = discussPostService.findDiscussPosts(0, page.getOffSet(), page.getLimit());
        List<Map<String,Object>> discussPosts = new ArrayList<>();
        if(list != null){
            for(DiscussPost post : list){
                Map<String,Object> map = new HashMap<>();
                map.put("post",post);
                User user = userService.findUserById(post.getUserId());
                map.put("user",user);
                discussPosts.add(map);
            }
        }
        model.addAttribute("discussPosts",discussPosts);
        return "/index";
    }


}
