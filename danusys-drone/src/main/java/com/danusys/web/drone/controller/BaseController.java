package com.danusys.web.drone.controller;

import com.danusys.web.commons.socket.config.CustomServerSocket;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
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
