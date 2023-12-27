package com.lsu.test.controller;

import com.lsu.spring.Annotation.Autowired;
import com.lsu.spring.Annotation.Controller;
import com.lsu.spring.Annotation.RequestMapping;
import com.lsu.spring.enums.RequestMethod;
import com.lsu.test.pojo.User;
import com.lsu.test.service.UserService;
import com.alibaba.fastjson.JSON;


/**
 * @author zt
 * @create 2023-12-24 23:33
 */
@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping("/")
    public String index(){
        return "index";             //转发到index.html
    }

    @RequestMapping(value = {"/hello","/toHello"},method = RequestMethod.GET)
    public String hello(){
        return "redirect:hello";    //重定向到/hello
    }

    @RequestMapping(value = {"/userInfo"})
    public String getUserInfo(String userId){
        User user = userService.getUserById(userId);
        return "json:" + JSON.toJSONString(user);    //返回JSON数据
    }
}
