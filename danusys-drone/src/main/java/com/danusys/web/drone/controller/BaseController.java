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



    @RequestMapping("/login/errorTest")
    public ModelAndView error() {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("view/pages/loginError");

        return mav;
    }

    /**
     * 권한이 있을경우와 없을경우를 나눠서 페이지 이동
     * @return
     */

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
