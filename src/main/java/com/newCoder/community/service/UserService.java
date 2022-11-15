package com.newCoder.community.service;

import com.newCoder.community.entity.LoginTicket;
import com.newCoder.community.entity.User;
import com.newCoder.community.vo.LoginVo;
import com.newCoder.community.vo.UpdateCodeVo;

import java.util.Map;

/**
 * @author lijie
 * @date 2022-11-09 15:47
 * @Desc
 */
public interface UserService {

    User findUserById(int id);

    User findUserByEmail(String email);

    Map<String,Object> regist(User user);

    int activation(Integer id,String activationCode);

    Map<String,Object> login(LoginVo user);

    void logout(String ticket);

    void sendCode(String email,String code);

    Map<String,Object> updateCode(String email, String password);

    LoginTicket findLoginTicket(String ticket);

    int updateHeaderUrl(int id, String headerUrl);

    Map<String, Object> updateCode(int uid, String salt,UpdateCodeVo vo);

    User findUserByUserName(String toName);
}
