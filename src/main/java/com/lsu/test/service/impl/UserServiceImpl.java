package com.lsu.test.service.impl;

import com.lsu.spring.Annotation.Autowired;
import com.lsu.spring.Annotation.Service;
import com.lsu.test.dao.UserMapper;
import com.lsu.test.pojo.User;
import com.lsu.test.service.UserService;

import java.util.ArrayList;

/**
 * @author zt
 * @create 2023-12-24 23:39
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;


    @Override
    public User getUserById(String userId) {
        return userMapper.getUserById(userId);
    }
}
