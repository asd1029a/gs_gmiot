package com.danusys.web.platform.controller;


import com.danusys.web.commons.auth.util.LoginInfoUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;


@RestController
@Slf4j
public class Join2Controller {

    @GetMapping("/test5")
    public Map<String,Object> test5(){
        Map<String,Object> loginInfoMap= new HashMap<>();
        loginInfoMap.put("userSeq",LoginInfoUtil.getUserDetails().getUserSeq());
        loginInfoMap.put("username",LoginInfoUtil.getUserDetails().getUsername());
        loginInfoMap.put("role",LoginInfoUtil.getUserDetails().getAuthorities());
        return loginInfoMap;

    }


    @RequestMapping("/login/error")
    public ModelAndView error() {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("view/error");

        return mav;
    }

    @RequestMapping("/permitAdminPage")
    public String permitAdminPage() {
        return "permitAdminPage";
    }

    @RequestMapping("/permitManagerPage")
    public String permitManagerPage() {
        return "permitManagerPage";
    }

    @RequestMapping("/permitAuthPage")
    public String permitAuthPage() {
        return "permitAuthPage";
    }


    @GetMapping("/permitAllPage")
    public String permitAllPage() {
        return "permitAllPage";
    }

    @RequestMapping("/loginpagetest")
    public ModelAndView loginpage() {

        ModelAndView mav = new ModelAndView();
        mav.setViewName("/layout/layout_login");

        return mav;
    }

    @RequestMapping("/roletest")
    public ModelAndView roletest() {

        ModelAndView mav = new ModelAndView();
        mav.setViewName("view/login/roletest");

        return mav;
    }

}
