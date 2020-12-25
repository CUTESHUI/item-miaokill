package com.shui.server.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.DisabledAccountException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Controller
public class UserController {

    @Autowired
    private Environment env;

    /**
     * 跳到登录页
     */
    @RequestMapping(value = {"/to/login","/unauth"})
    public String toLogin(){
        return "login";
    }

    /**
     * 登录认证
     * 通用
     */
    @PostMapping("/login")
    public String login(@RequestParam String userName, @RequestParam String password, ModelMap modelMap){
        String errorMsg="";
        try {
            // 认证不通过做登录
            if (!SecurityUtils.getSubject().isAuthenticated()){
                String newPsd = new Md5Hash(password, env.getProperty("shiro.encrypt.password.salt")).toString();
                UsernamePasswordToken token = new UsernamePasswordToken(userName, newPsd);
                // login(AuthenticationToken var1)
                SecurityUtils.getSubject().login(token);
            }
        } catch (UnknownAccountException e){
            errorMsg = e.getMessage();
            modelMap.addAttribute("userName",userName);

        } catch (DisabledAccountException e){
            errorMsg = e.getMessage();
            modelMap.addAttribute("userName",userName);

        } catch (IncorrectCredentialsException e){
            errorMsg = e.getMessage();
            modelMap.addAttribute("userName",userName);

        } catch (Exception e){
            errorMsg = "用户登录异常，请联系管理员!";
            e.printStackTrace();
        }
        // 没有错误，登录认证成功
        if (StringUtils.isBlank(errorMsg)){
            return "redirect:/index";
        } else{
            modelMap.addAttribute("errorMsg",errorMsg);
            return "login";
        }
    }

    /**
     * 退出
     */
    @RequestMapping(value = "/logout")
    public String logout(){
        SecurityUtils.getSubject().logout();
        return "login";
    }


}
