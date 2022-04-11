package com.danusys.web.demo.controller;


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

    @RequestMapping("/login/error")
    public ModelAndView error() {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("loginError2");

        return mav;
    }

    @RequestMapping("/test2")
    public String test2() {
        return "test2";
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


    @RequestMapping("/permitAllPage")
    public String permitAllPage() {
        return "permitAllPage";
    }

    @RequestMapping("/loginpage")
    public ModelAndView loginpage(HttpServletRequest request, HttpServletResponse response) {
//       Cookie cookie =new Cookie("accessToken",null);
//        cookie.setMaxAge(0);
//        response.addCookie(cookie);

        ModelAndView mav = new ModelAndView();
        mav.setViewName("view/login/test");

        return mav;
    }


}
