//package com.danusys.web.smartmetering.config;
//
//import com.danusys.web.commons.auth.session.interceptor.AuthInterceptor;
//import com.danusys.web.commons.auth.session.interceptor.CommonInterceptor;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
//@Configuration
//public class SmartmeteringMvcConfiguration implements WebMvcConfigurer {
//
//    @Override
//    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(new AuthInterceptor())
//                .addPathPatterns("/**/*.do","/**/*.ado")
//                .excludePathPatterns("/admin/loginForm.do", "/admin/loginProc.ado", "/admin/logoutProc.do", "/common/error.do");
//
//        registry.addInterceptor(new CommonInterceptor())
//                .addPathPatterns("/**/*.do","/**/*.ado");
//
//    }
//
//
//
//}
