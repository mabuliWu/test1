package com.jutiyunyuan.controller;

import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * @Author Kong
 * @Date 2020/10/26 19:41
 * @Version 1.0
 */

@Controller
@RequestMapping("/admin")
public class AdminController {
    /*Shiro认证失败后跳转的页面*/
    @RequestMapping("/loginError")
    public String loginError(HttpServletRequest request, Model model) {
        System.out.println("AdminController.loginError");
        //获取错误信息
        String shiroLoginFailure = (String) request.getAttribute("shiroLoginFailure");
        System.out.println("shiroLoginFailure = " + shiroLoginFailure);

        if (UnknownAccountException.class.getName().equals(shiroLoginFailure) ) {
            model.addAttribute("errorMsg","用户不存在");
        }
        if (IncorrectCredentialsException.class.getName().equals(shiroLoginFailure)) {
            model.addAttribute("errorMsg","密码错误");
        }
        return "login";
    }


    @RequestMapping("/login")
    public String login() {
        return "login";
    }
}
