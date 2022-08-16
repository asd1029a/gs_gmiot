package com.danusys.web.firefighting.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
public class BaseController {

    @Value("${homePage.url}")
    private String homePageUrl;

    @Value("${loginPage.path}")
    private String loginPagePath;

    @RequestMapping(value = "/")
    public String index() {
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            log.info("principal={}",principal);
            if (!principal.toString().equals("anonymousUser"))
                return "redirect:" + homePageUrl;
        }

        return loginPagePath;
    }
}
