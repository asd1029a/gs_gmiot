//package com.danusys.web.platform.controller;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.web.servlet.error.ErrorController;
//import org.springframework.http.HttpStatus;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.RequestMapping;
//
//import javax.servlet.RequestDispatcher;
//import javax.servlet.http.HttpServletRequest;
//
//@Controller
//public class CustomErrorController implements ErrorController {
//    private String VIEW_PATH = "/errors/";
//
//
//    @Value("${defaultFailureUrl}")
//    private String defaultFailureUrl;
//
//    @RequestMapping(value = "/error")
//    public String handleError(HttpServletRequest request) {
//        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
//        if (status != null) {
//            int statusCode = Integer.valueOf(status.toString());
//            if (statusCode == HttpStatus.NOT_FOUND.value()) {
//              // return VIEW_PATH + "404";
//                return "/view/login/loginErrorTest";
//            }
//            if (statusCode == HttpStatus.FORBIDDEN.value()) {
//              //  return VIEW_PATH + "500";
//                return "/view/login/loginErrorTest";
//            }
//        }
//        return "error";
//    }
//
//    @Override
//    public String getErrorPath() {
//        return null;
//    }
//}
//
