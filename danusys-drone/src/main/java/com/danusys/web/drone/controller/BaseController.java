package com.danusys.web.drone.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequiredArgsConstructor
@Slf4j
public class BaseController {


    @Value("${homePage.url}")
    private String homePageUrl;

    @Value("${loginPage.path}")
    private String loginPagePath;
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


    @RequestMapping(value = "/")
    public String index() {

        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            log.info("principal={}", principal);
            if (!principal.toString().equals("anonymousUser"))
                return "redirect:" + homePageUrl;
        }

        return loginPagePath;
    }


}
