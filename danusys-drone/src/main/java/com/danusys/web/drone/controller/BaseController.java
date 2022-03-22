package com.danusys.web.drone.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class BaseController {

//    @RequestMapping(value = "/")
//    public String index() {
//        return "/layout/layout_login";
//    }


    @RequestMapping("/login/errorTest")
    public ModelAndView error() {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("view/pages/loginError");

        return mav;
    }
}
