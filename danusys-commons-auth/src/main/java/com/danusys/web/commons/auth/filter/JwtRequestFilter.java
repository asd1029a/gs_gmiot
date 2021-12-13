package com.danusys.web.commons.auth.filter;


import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.danusys.web.commons.auth.config.auth.CommonsUserDetailsService;
import com.danusys.web.commons.auth.model.User;
import com.danusys.web.commons.auth.util.JwtUtil;
import com.netflix.zuul.context.RequestContext;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.IOException;
import java.util.Date;
import java.util.stream.Collectors;


@Slf4j


public class JwtRequestFilter extends OncePerRequestFilter {
    //public class JwtRequestFilter extends OncePerRequestFilter {


    @Autowired
    private CommonsUserDetailsService userDetailsService;


    @Autowired
    private JwtUtil jwtUtil;

    @Bean
    public FilterRegistrationBean filterRegistrationBean() {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        registrationBean.setFilter(new JwtRequestFilter());
        registrationBean.setEnabled(false);
        return registrationBean;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {





       String authorizationHeader = request.getHeader("Authorization");
        String refreshToken = request.getHeader("RefreshHeader");

        //String authorizationHeader = null;
      //  String refreshToken = null;

      // String url = request.getRequestURI();
     //   log.info("headera={}",response.getHeader("a"));
  //      log.info("header1={}", authorizationHeader);
  //      log.info("header2={}", refreshToken);
   //     log.info("header3={}", request.getHeader("a"));

        Cookie[] cookies=request.getCookies();
        String accessTokenValue;
      //  if(cookies==null)
      //      log.info("where is cookies");
        if(cookies!= null){
            for(Cookie cookie : cookies){
                // log.info(cookie.getName());
                //   log.info(cookie.getValue());
                if(cookie.getName().equals("accessToken")) {
                    authorizationHeader = "Bearer " + cookie.getValue();

                    log.info("cookie.getValue()={}",authorizationHeader.substring(7));
                }

                if(cookie.getName().equals("refreshToken"))
                    refreshToken= cookie.getValue();
                    log.info("refreshToken={}",refreshToken);
            }
        }

        //log.info("authrizationHeader={}",authorizationHeader);
        String username = null;
        String jwt = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {

            jwt = authorizationHeader.substring(7); //beaer 뒤에 붙은것들


            username = jwtUtil.extractUsername(jwt); //extractUsername에서 유효기간이 지났다면 exception 발생

        }

//  1.AccessToken 유효성 체크
        log.info("username={}", username);
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            //2.토큰 사용자 조회

            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
            log.info("userDetails={}", userDetails);
            log.info("2");

            DecodedJWT decodedJWT = JWT.decode(jwt);

            //log.info("abc={}",abc.getExpiresAt());
            //if(jwtUtil.isTokenExpired(authorizationHeader.substring(7)) ){
            /*
            if(decodedJWT.getExpiresAt().before(new Date())){
                //      log.info("-----------");
                //    log.info("refreshToken={}",refreshToken);
                //  log.info("getUsername={}",userDetails.getUsername());
                if(jwtUtil.validateToken( refreshToken,userDetails)){
                    //3.발급
                    jwt=jwtUtil.generateToken(userDetails).getAccessToken();
                    Cookie cookie=new Cookie("accessToken",jwt);
                    response.addCookie(cookie);
                    //  log.info("new jwt ={}",jwt);

                }
            }
            */

            if (jwtUtil.validateToken(jwt, userDetails)) {
                log.info("test");
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                usernamePasswordAuthenticationToken
                        .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));


                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);

            }
        }


        chain.doFilter(request, response);
    }

}
