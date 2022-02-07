package com.danusys.web.drone.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/drone/test")
public class DronePageContoller {


    @RequestMapping("/index")
    public ModelAndView Index() {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("/test/index.html");
        return mv;
    }

    @RequestMapping("/createMission")
    public ModelAndView CreateMisson() {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("/test/createMisson.html");
        return mv;
    }

}
