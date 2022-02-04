package com.danusys.web.platform.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@RestController
@Slf4j
public class Join2Controller {


    @RequestMapping("/hi234")
    public String hi1234() {
        return "hi";
    }



    @RequestMapping("/login/errorTest")
    public ModelAndView error() {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("/view/login/loginErrorTest");

        return mav;
    }

    @RequestMapping("/hi")
    public String hi(){
        return "hi";
    }

    @RequestMapping("/test2")
    public String test2() {
        return "test2";
    }

    @RequestMapping("/permitadminpage")
    public String permitadminpage() {
        return "permitadminpage";
    }

    @RequestMapping("/permitmanagerpage")
    public String permitmanagerpage() {
        return "permitmanagerpage";
    }

    @RequestMapping("/permitauthpage")
    public String permitauthpage() {
        return "permitauthpage";
    }


    @RequestMapping("/permitallpage")
    public String permitallpage() {
        return "permitallpage";
    }

    @RequestMapping("/loginpagetest")
    public ModelAndView loginpage() {

        ModelAndView mav = new ModelAndView();
        mav.setViewName("/view/login/test1");

        return mav;
    }

    @RequestMapping("/roletest")
    public ModelAndView roletest() {

        ModelAndView mav = new ModelAndView();
        mav.setViewName("/view/login/roletest");

        return mav;
    }

}
