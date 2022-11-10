package com.newCoder.community.service;

import com.newCoder.community.entity.User;
import org.springframework.stereotype.Service;

/**
 * @author lijie
 * @date 2022-11-09 15:47
 * @Desc
 */
public interface UserService {

    User findUserById(int id);
}
