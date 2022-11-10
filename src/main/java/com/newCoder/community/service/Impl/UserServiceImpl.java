package com.newCoder.community.service.Impl;

import com.newCoder.community.dao.UserMapper;
import com.newCoder.community.entity.User;
import com.newCoder.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author lijie
 * @date 2022-11-09 21:06
 * @Desc
 */
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;

    public User findUserById(int id){
        return  userMapper.selectById(id);
    }
}
