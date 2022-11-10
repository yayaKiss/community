package com.newCoder.community.dao;

import com.newCoder.community.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author lijie
 * @date 2022-11-09 00:33
 * @Desc
 */
@Mapper
public interface UserMapper {

    User selectById(@Param("id") int id);

    User selectByName(@Param("username") String username);

    User selectByEmail(String email);

    int insertUser(User user);

    int updateStatus(@Param("id") int id,@Param("status") int status);

    int updateHeaderUrl(@Param("id") int id,@Param("headerUrl") String headerUrl);

    int updatePassword(@Param("id") int id,@Param("password") String password);

}
