package com.newCoder.community.controller;

import com.google.code.kaptcha.Producer;
import com.newCoder.community.constant.ActivationConstant;
import com.newCoder.community.constant.EntityConstant;
import com.newCoder.community.constant.UserExpireConstant;
import com.newCoder.community.entity.DiscussPost;
import com.newCoder.community.entity.Page;
import com.newCoder.community.entity.User;
import com.newCoder.community.service.DiscussPostService;
import com.newCoder.community.service.LikeService;
import com.newCoder.community.service.UserService;
import com.newCoder.community.util.CommunityUtils;
import com.newCoder.community.util.HostHolder;
import com.newCoder.community.util.JsonResult;
import com.newCoder.community.util.RedisKeyUtils;
import com.newCoder.community.vo.LoginVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


import javax.imageio.ImageIO;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;


/**
 * @author lijie
 * @date 2022-11-09 16:14
 * @Desc
 */
@Slf4j
@Controller
public class HomeController {
    @Autowired
    private UserService userService;
    @Autowired
    private DiscussPostService discussPostService;
    @Autowired
    private Producer kaptcha;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private LikeService likeService;
    @Autowired
    private HostHolder hostHolder;

    @Value("${server.servlet.context-path}")
    private String contextPath;


    @GetMapping({"/index","/"})
    //???springMVC???????????????dispatchServlet??????????????????????????????page????????????model???
    public String index(Model model, Page page,@RequestParam(value = "orderMode",defaultValue = "0") int orderMode){
        page.setRows(discussPostService.findDiscussPostRows(0));
        page.setPath("/index?orderMode=" + orderMode);

        List<DiscussPost> list = discussPostService.findDiscussPosts(0, page.getOffset(), page.getLimit(), orderMode);
        List<Map<String,Object>> discussPosts = new ArrayList<>();
        if(list != null){
            for(DiscussPost post : list){
                Map<String,Object> map = new HashMap<>();
                //??????
                map.put("post",post);
                //?????????????????????
                long likeCount = likeService.findEntityLikeCount(EntityConstant.ENTITY_TYPE_POST, post.getId());
                int likeStatus = hostHolder.getValue() == null ? 0 :
                        likeService.findEntityLikeStatus(hostHolder.getValue().getId(), EntityConstant.ENTITY_TYPE_POST, post.getId());
                map.put("likeCount",likeCount);
                map.put("likeStatus",likeStatus);
                //??????
                User user = userService.findUserById(post.getUserId());
                map.put("user",user);
                discussPosts.add(map);
            }
        }
        model.addAttribute("discussPosts",discussPosts);
        model.addAttribute("orderMode",orderMode);
        return "/index";
    }

    @PostMapping("/regist")
    public String regist(Model model ,User user){
        Map<String ,Object> map = userService.regist(user);
        if(map == null || map.isEmpty()){ //????????????
            model.addAttribute("msg", "????????????????????????????????????????????????????????????????????????????????????");
            model.addAttribute("target",contextPath + "/index");
            return "/site/operate-result";
        }else{
            model.addAttribute("usernameMsg",map.get("usernameMsg"));
            model.addAttribute("passwordMsg",map.get("passwordMsg"));
            model.addAttribute("emailMsg",map.get("emailMsg"));
            return "site/register";
        }
    }

    @GetMapping("/activation/{id}/{code}")
    public String activation(Model model, @PathVariable("id") Integer id,@PathVariable("code") String activeCode){
        int res = userService.activation(id, activeCode);
        if(res == ActivationConstant.ACTIVATION_SUCCESS){
            model.addAttribute("msg","????????????,?????????????????????????????????!");
            model.addAttribute("target",contextPath + "/login.html");
        }else if(res == ActivationConstant.ACTIVATION_REPEAT){
            model.addAttribute("msg","????????????????????????!");
            model.addAttribute("target",contextPath +"/login.html");
        }else{
            model.addAttribute("msg","????????????,???????????????????????????!");
            model.addAttribute("target",contextPath + "/index");
        }
        return "/site/operate-result";
    }

    @GetMapping("/kaptcha")
    public void getKaptcha(HttpServletResponse response /**HttpSession session**/){
        //????????????????????????
        String text = kaptcha.createText();
        BufferedImage image = kaptcha.createImage(text);

//        //???????????????????????????
//        session.setAttribute("kaptcha",text);
        //??????????????????redis??????cookie?????????owner??????????????????????????????
        String kaptchaOwner = CommunityUtils.generateUUID().toString();
        Cookie cookie = new Cookie("kaptchaOwner",kaptchaOwner);
        cookie.setPath(contextPath);
        cookie.setMaxAge(60);
        response.addCookie(cookie);
        String redisKey = RedisKeyUtils.getKaptchaKey(kaptchaOwner);
        redisTemplate.opsForValue().set(redisKey,text,60,TimeUnit.SECONDS);
        //???????????????????????????
        response.setContentType("image/png");
        try {
            OutputStream os = response.getOutputStream();
            ImageIO.write(image,"png",os);
        } catch (IOException e) {
            log.info("????????????????????? !" + e.getMessage());
        }
    }


    @PostMapping("/login")
    public String login(LoginVo user, /**HttpSession session,**/ Model model
                        ,HttpServletResponse response ,@CookieValue("kaptchaOwner")String kaptchaOwner){
        String code = user.getCode();
//        String kaptcha = (String) session.getAttribute("kaptcha");
        //???redis???????????????
        String kaptcha = null;
        if(!StringUtils.isEmpty(kaptchaOwner)){
            String redisKey = RedisKeyUtils.getKaptchaKey(kaptchaOwner);
            kaptcha = (String) redisTemplate.opsForValue().get(redisKey);
        }

        //???????????????
        if(StringUtils.isEmpty(code) || StringUtils.isEmpty(kaptcha) || !code.equalsIgnoreCase(kaptcha)){
            model.addAttribute("codeMsg","???????????????");
            return "/site/login";
        }
        //?????????????????????
        Map<String, Object> map = userService.login(user);
        if(!map.containsKey("ticket")){ //????????????
            model.addAttribute("usernameMsg",map.get("usernameMsg"));
            model.addAttribute("passwordMsg",map.get("passwordMsg"));
            return "/site/login";
        }
        int expireTime = user.isRememberme() ? UserExpireConstant.REMEMBER_EXPIRE_TIME : UserExpireConstant.DEFAULT_EXPIRE_TIME;
        Cookie cookie = new Cookie("ticket",(String) map.get("ticket"));
        cookie.setMaxAge(expireTime);
        cookie.setPath(contextPath);
        response.addCookie(cookie);
        return "redirect:/index";
    }

    @GetMapping("/logout")
    public String logout(@CookieValue("ticket") String ticket,HttpServletRequest request){
        userService.logout(ticket);
        SecurityContextHolder.clearContext();
        return "redirect:/index";
    }

    @PostMapping("/requireCode")
    @ResponseBody
    public JsonResult forgetCode(@RequestParam("email") String email){
        User user = userService.findUserByEmail(email);
        if(user == null){
            return JsonResult.error("????????????????????????");
        }
        String redisKey = RedisKeyUtils.getEmailCode(email);
        String codeAndTime = (String) redisTemplate.opsForValue().get(redisKey);
        if(!StringUtils.isEmpty(codeAndTime)){
            long time = Long.parseLong(codeAndTime.split("-")[1]);
            if(System.currentTimeMillis() - time < 60 * 1000){
                return JsonResult.error("???????????????????????????????????????");
            }
        }
        String code = CommunityUtils.generateUUID().substring(0,5);
        redisTemplate.opsForValue().set(redisKey,code + "-" + System.currentTimeMillis()
                ,300, TimeUnit.SECONDS);
        CompletableFuture<Void> forgetFuture = CompletableFuture.runAsync(() -> {
            userService.sendCode(email,code);
        });
        return JsonResult.ok("????????????????????????????????????????????????");
    }

    @PostMapping("/updateCode")
    public String updateCode(String email,String code,String password,Model model){
        //user:code:email
        String redisKey = RedisKeyUtils.getEmailCode(email);
        String codeAndTime = (String) redisTemplate.opsForValue().get(redisKey);
        //???????????????
        if(StringUtils.isEmpty(code) || StringUtils.isEmpty(codeAndTime) || !code.equals(codeAndTime.split("-")[0])){
            model.addAttribute("codeMsg","???????????????");
            return "/site/forget";
        }
        //?????????????????????
        Map<String, Object> map = userService.updateCode(email, password);
        if (!map.isEmpty()){
            model.addAttribute("emailMsg",map.get("emailMsg"));
            model.addAttribute("passwordMsg", map.get("passwordMsg"));
            return "/site/forget";
        }

        return "redirect:/login.html";
    }



    @GetMapping("/error")
    public String getErrorPage(){
        return "/error/500";
    }

    @GetMapping("/denied")
    public String getDeniedPage(){
        return "/error/404";
    }

}
