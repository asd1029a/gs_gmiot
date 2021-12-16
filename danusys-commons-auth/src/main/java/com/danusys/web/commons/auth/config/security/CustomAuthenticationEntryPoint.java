package com.danusys.web.commons.auth.config.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.*;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.naming.AuthenticationException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Slf4j
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {


    private String defaultFailureUrl;
/*
    public CustomAuthenticationEntryPoint(String defaultFailureUrl) {
        this.defaultFailureUrl = defaultFailureUrl;
    }
*/
    public CustomAuthenticationEntryPoint() {
        this.defaultFailureUrl = "/login/error";
    }


    @Override
    public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, org.springframework.security.core.AuthenticationException authenticationException) throws IOException, ServletException {
        String defaultFailureUrl = this.defaultFailureUrl;
        log.trace("error type ={}",authenticationException.getClass().getName());
        if(authenticationException instanceof AuthenticationServiceException) {
            log.error("AuthenticationServiceException");
            httpServletRequest.setAttribute("failMsg", "가입되지 않은 사용자");
        } else if(authenticationException instanceof BadCredentialsException) {
            log.error("BadCredentialsException");
            httpServletRequest.setAttribute("failMsg", "아이디 또는 비밀번호가 틀렸습니다.");
        } else if(authenticationException instanceof LockedException) {
            log.error("LockedException");
            httpServletRequest.setAttribute("failMsg", "계정이 잠겨있습니다. 관리자에게 문의하십시오.");
        } else if(authenticationException instanceof DisabledException) {
            log.error("DisabledException");
            httpServletRequest.setAttribute("failMsg", "비황성화된 계정입니다.");
        } else if(authenticationException instanceof AccountExpiredException) {
            log.error("AccountExpiredException");
            httpServletRequest.setAttribute("failMsg", "기간 만료된 계정입니다.");
        } else if(authenticationException instanceof CredentialsExpiredException) {
            log.error("CredentialsExpiredException");
            httpServletRequest.setAttribute("failMsg", "비밀번호 틀렸습니다.");
        }  else if(authenticationException instanceof InsufficientAuthenticationException) {
            log.error("InsufficientAuthenticationException");
            httpServletRequest.setAttribute("failMsg", "인증정보가 부족합니다.");
        }

        log.trace("# defaultFailureUrl : {} ", defaultFailureUrl);
        httpServletRequest.getRequestDispatcher(defaultFailureUrl).forward(httpServletRequest,httpServletResponse);
        //httpServletResponse.sendRedirect(defaultFailureUrl).forward();
    }
}