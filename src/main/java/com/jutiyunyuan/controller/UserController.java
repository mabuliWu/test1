package com.jutiyunyuan.controller;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @Author Kong
 * @Date 2020/10/26 22:39
 * @Version 1.0
 */
@Controller
@RequestMapping("/user")
public class UserController {


//    @RequiresRoles()  根据角色判断, 不利于维护
    @ResponseBody
    @RequestMapping("/list")
    @RequiresPermissions("user:list")
    public String list() {
        return "张三, 李四, 王五";
    }

    @RequestMapping("/insert")
    @RequiresPermissions("user:insert")
    @ResponseBody
    public String insert() {
        return "新增成功";
    }
}
