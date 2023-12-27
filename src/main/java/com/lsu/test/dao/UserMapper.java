package com.lsu.test.dao;


import com.lsu.test.pojo.User;

import java.util.ArrayList;

/**
 * @author zt
 * @create 2023-12-24 23:21
 */
public interface UserMapper {
    User getUserById(String userId);
}
