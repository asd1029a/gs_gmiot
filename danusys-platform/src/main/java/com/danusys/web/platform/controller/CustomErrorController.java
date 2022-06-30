package com.danusys.web.platform.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

@Controller
public class CustomErrorController implements ErrorController {
    private String VIEW_PATH = "view/error";

    @RequestMapping(value = "/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        int statusCode = Integer.parseInt(status.toString());
        HttpStatus httpStatus = HttpStatus.valueOf(statusCode);

        if(status != null) {
            model.addAttribute("code", status.toString());
            if(statusCode == 404) {
                model.addAttribute("message", "페이지를 찾을 수 없습니다.");
            } else if(statusCode == 500) {
                model.addAttribute("message", "서버내 처리중 에러가 발생했습니다." + httpStatus.getReasonPhrase());
            } else if(statusCode == 403) {
                model.addAttribute("message", "토큰이 만료되었거나, 권한이 없습니다.");
            } else {
                model.addAttribute("message", httpStatus.getReasonPhrase());
            }
        }

        return VIEW_PATH;
    }

    @Override
    public String getErrorPath() {
        return null;
    }
}

