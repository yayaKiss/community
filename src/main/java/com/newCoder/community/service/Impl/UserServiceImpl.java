package com.newCoder.community.service.Impl;

import com.newCoder.community.constant.ActivationConstant;
import com.newCoder.community.constant.UserExpireConstant;
import com.newCoder.community.dao.LoginTicketMapper;
import com.newCoder.community.dao.UserMapper;
import com.newCoder.community.entity.LoginTicket;
import com.newCoder.community.entity.User;
import com.newCoder.community.service.UserService;
import com.newCoder.community.util.CommunityUtils;
import com.newCoder.community.util.QQMailClient;
import com.newCoder.community.vo.LoginVo;
import com.newCoder.community.vo.UpdateCodeVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CompletableFuture;


/**
 * @author lijie
 * @date 2022-11-09 21:06
 * @Desc
 */
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private QQMailClient mailClient;
    @Autowired
    private TemplateEngine templateEngine;
    @Autowired
    private LoginTicketMapper loginTicketMapper;

    @Value("${community.path.domain}")
    private String domain;
    @Value("${server.servlet.context-path}")
    private String contextPath;

    public User findUserById(int id){
        return  userMapper.selectById(id);
    }

    @Override
    public User findUserByEmail(String email) {
        return userMapper.selectByEmail(email);
    }

    @Override
    public Map<String,Object> regist(User user) {
        Map<String,Object> map = new HashMap<>();
        if(StringUtils.isEmpty(user.getUsername())){
            map.put("usernameMsg","用户名不能为空");
            return map;
        }
        if(StringUtils.isEmpty(user.getUsername())){
            map.put("passwordMsg","密码不能为空");
            return map;
        }
        if(StringUtils.isEmpty(user.getUsername())){
            map.put("emailMsg","邮箱不能为空");
            return map;
        }
        //验证用户名
        if(userMapper.selectByName(user.getUsername()) != null){
            map.put("usernameMsg","用户名已存在");
            return map;
        }
        //验证邮箱
        if(userMapper.selectByEmail(user.getEmail()) != null){
            map.put("emailMsg","邮箱已被注册");
            return map;
        }

        //注册用户
        user.setSalt(CommunityUtils.generateUUID().substring(0,5));
        user.setPassword(CommunityUtils.MD5(user.getPassword() + user.getSalt()));
        user.setType(0);
        user.setStatus(0);
        user.setActivationCode(CommunityUtils.generateUUID());
        user.setHeaderUrl(String.format("http://images.newcoder.com/head/%dt.png",new Random().nextInt(1000)));
        user.setCreateTime(new Date());
        userMapper.insertUser(user);


        CompletableFuture<Void> sendMailFuture = CompletableFuture.runAsync(() -> {
            //发送激活邮件
            Context context = new Context();
            context.setVariable("username",user.getUsername());
            // http://localhost:8080/activation/id/activationCode
            String url = domain + contextPath +  "/activation/" +  user.getId() + "/" + user.getActivationCode();
            context.setVariable("url",url);
            String content = templateEngine.process("/mail/activation",context);

            mailClient.sendMessage(user.getEmail(),"激活账号",content);
        });

        return map;
    }

    @Override
    public int activation(Integer id, String activationCode) {
        User user = userMapper.selectById(id);
        if(user.getStatus() == 1){
            return ActivationConstant.ACTIVATION_REPEAT;
        }else if(activationCode.equals(user.getActivationCode())){
            userMapper.updateStatus(id,1);
            return ActivationConstant.ACTIVATION_SUCCESS;
        }else{
            return ActivationConstant.ACTIVATION_FAILURE;
        }
    }

    @Override
    public Map<String,Object> login(LoginVo user) {
        Map<String,Object> map = new HashMap<>();
        String username = user.getUsername();
        String password = user.getPassword();
        //空值处理
        if(StringUtils.isEmpty(username)){
            map.put("usernameMsg","用户名不能为空");
            return map;
        }
        if(StringUtils.isEmpty(password)){
            map.put("passwordMsg","密码不能为空");
            return map;
        }
        //验证账号
        User u = userMapper.selectByName(username);
        if(u == null){
            map.put("usernameMsg","用户名错误");
            return map;
        }
        if(u.getStatus() == 0) {
            map.put("usernameMsg", "账号未激活");
            return map;
        }
        password = CommunityUtils.MD5(password + u.getSalt());
        if(!u.getPassword().equals(password)){
            map.put("passwordMsg","密码错误");
            return map;
        }
        String ticket = CommunityUtils.generateUUID();
        Long expiredTime = (long)(user.isRememberme() ?
                UserExpireConstant.REMEMBER_EXPIRE_TIME : UserExpireConstant.DEFAULT_EXPIRE_TIME);
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setTicket(ticket);
        loginTicket.setUserId(u.getId());
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis() + expiredTime * 1000));

        loginTicketMapper.insertLoginTicket(loginTicket);
        map.put("ticket",ticket);
        return map;
    }

    @Override
    public void logout(String ticket) {
        loginTicketMapper.updateStatus(ticket,1);
    }

    @Override
    public void sendCode(String email,String code) {
        Context context = new Context();
        context.setVariable("email",email);
        context.setVariable("code",code);
        String content = templateEngine.process("/mail/forget", context);

        mailClient.sendMessage(email,"修改密码",content);
    }

    @Override
    public Map<String,Object> updateCode(String email,String password) {
        Map<String,Object> map = new HashMap<>();
        if(StringUtils.isEmpty(email)){
            map.put("emailMsg","邮箱不能为空");
            return map;
        }
        if(StringUtils.isEmpty(password)){
            map.put("passwordMsg","密码不能为空");
            return map;
        }
        User user = userMapper.selectByEmail(email);
        password = CommunityUtils.MD5(password + user.getSalt());
        userMapper.updatePassword(user.getId(),password);

        return map;
    }

    @Override
    public LoginTicket findLoginTicket(String ticket) {
        return loginTicketMapper.selectByTicket(ticket);
    }

    @Override
    public int updateHeaderUrl(int id, String headerUrl) {
        return userMapper.updateHeaderUrl(id,headerUrl);
    }

    @Override
    public Map<String, Object> updateCode(int uid, String salt,UpdateCodeVo vo) {
        Map<String,Object> map = new HashMap<>();
        String newPassword = vo.getNewPassword();
        String confirmPassword = vo.getConfirmPassword();
        //判空
        if(StringUtils.isEmpty(newPassword)){
            map.put("newMsg","密码不能为空");
            return map;
        }
        //两次密码不一致
        if(!newPassword.equals(confirmPassword)){
            map.put("confirmMsg","两次输入的面不一致");
            return map;
        }
        newPassword = CommunityUtils.MD5(newPassword + salt);
        userMapper.updatePassword(uid,newPassword);
        return map;
    }
}
