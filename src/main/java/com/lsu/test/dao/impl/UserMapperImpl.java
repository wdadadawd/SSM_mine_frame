package com.lsu.test.dao.impl;

import com.lsu.spring.Annotation.Mapper;
import com.lsu.spring.base.BaseDAO;
import com.lsu.test.dao.UserMapper;
import com.lsu.test.pojo.User;

import java.util.ArrayList;

/**
 * @author zt
 * @create 2023-12-24 23:23
 */
@Mapper
public class UserMapperImpl extends BaseDAO<User> implements UserMapper {

    @Override
    public User getUserById(String userId) {
        String sql = "SELECT * FROM user WHERE user_id = ?";
        ArrayList<User> userArrayList = query(sql,userId);
        if (userArrayList != null)
            return userArrayList.get(0);
        return null;
    }
}
