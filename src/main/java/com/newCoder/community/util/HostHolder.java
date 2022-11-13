package com.newCoder.community.util;

import com.newCoder.community.entity.User;
import org.springframework.stereotype.Component;

/**
 * @author lijie
 * @date 2022-11-12 16:49
 * @Desc
 */
@Component
public class HostHolder {
    private final ThreadLocal<User> threadLocal = new ThreadLocal<>();

    public void setValue(User user){
        threadLocal.set(user);
    }
    public User getValue(){
        return threadLocal.get();
    }
    public void remove(){
        threadLocal.remove();
    }
}
