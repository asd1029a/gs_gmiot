package com.danusys.web.commons.auth.session.config.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.*;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.stereotype.Component;

import javax.naming.AuthenticationException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Slf4j
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Value("${defaultFailureUrl}")
    private String defaultFailureUrl;

//    @Value("${defaultHomeUrl}")
//    private String defaultHomeUrl;

    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
/*z
    public CustomAuthenticationEntryPoint(String defaultFailureUrl) {
        this.defaultFailureUrl = defaultFailureUrl;
    }
*/
    //public CustomAuthenticationEntryPoint() {
    // this.defaultFailureUrl = "/login/error";
    // }


    @Override
    public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, org.springframework.security.core.AuthenticationException authenticationException) throws IOException, ServletException {
        String defaultFailureUrl = this.defaultFailureUrl;
        log.trace("error type ={}", authenticationException.getClass().getName());
        String exception=null;
                exception=(String) httpServletRequest.getAttribute("exception");
        log.info("log : exception:{}",exception);
        if(exception!=null){
            if(exception.equals("ExpiredJwtException")){
                log.error("ExpiredJwtException");
                httpServletRequest.setAttribute("failMsg", "토큰만료.");
            }
        }


        if (authenticationException instanceof AuthenticationServiceException) {
            log.error("AuthenticationServiceException");

           httpServletRequest.setAttribute("failMsg", "가입되지 않은 사용자");
           // setResponse(httpServletResponse,"가입되지 않은 사용자");
        } else if (authenticationException instanceof BadCredentialsException) {
            log.error("BadCredentialsException");
            httpServletRequest.setAttribute("failMsg", "아이디 또는 비밀번호가 틀렸습니다.");
           // setResponse(httpServletResponse,"가입되지 않은 사용자");
        } else if (authenticationException instanceof LockedException) {
            log.error("LockedException");
           httpServletRequest.setAttribute("failMsg", "계정이 잠겨있습니다. 관리자에게 문의하십시오.");
           // setResponse(httpServletResponse,"가입되지 않은 사용자");
        } else if (authenticationException instanceof DisabledException) {
            log.error("DisabledException");
            httpServletRequest.setAttribute("failMsg", "비황성화된 계정입니다.");
          //  setResponse(httpServletResponse,"가입되지 않은 사용자");
        } else if (authenticationException instanceof AccountExpiredException) {
            log.error("AccountExpiredException");
            httpServletRequest.setAttribute("failMsg", "기간 만료된 계정입니다.");
           // setResponse(httpServletResponse,"가입되지 않은 사용자");
        } else if (authenticationException instanceof CredentialsExpiredException) {
            log.error("CredentialsExpiredException");
            httpServletRequest.setAttribute("failMsg", "비밀번호 틀렸습니다.");
            //setResponse(httpServletResponse,"가입되지 않은 사용자");
        } else if (authenticationException instanceof InsufficientAuthenticationException) {
            log.error("InsufficientAuthenticationException");
            //  log.trace("# defaultHomeUrl : {} ", defaultHomeUrl);
            httpServletRequest.setAttribute("failMsg", "인증정보가 부족합니다.");
           // setResponse(httpServletResponse,"가입되지 않은 사용자");
            //     httpServletResponse.sendRedirect(defaultHomeUrl);
        }

        log.trace("# defaultFailureUrl : {} ", defaultFailureUrl);

       // RequestDispatcher dispatcher = httpServletRequest.getRequestDispatcher(defaultFailureUrl);
       // dispatcher.forward(httpServletRequest, httpServletResponse);
        // httpServletRequest.getRequestDispatcher("/login/error").forward(httpServletRequest,httpServletResponse);



        httpServletResponse.sendRedirect(defaultFailureUrl);

    }


}