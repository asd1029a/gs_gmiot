package com.danusys.web.commons.auth.filter;


import com.danusys.web.commons.auth.config.auth.CommonsUserDetailsService;
import com.danusys.web.commons.auth.util.JwtUtil;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;


@Slf4j
public class JwtRequestFilter extends OncePerRequestFilter {


    @Autowired
    private CommonsUserDetailsService userDetailsService;

    @Value("${defaultFailureUrl}")
    private String defaultFailureUrl;

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

        final String uri = request.getRequestURI();

//        log.info("request.getRequestURI() : {}", request.getRequestURI());

        if (uri.contains("/t1-daumcdn-net/") || uri.contains("/webjar/")) {
        } else {
            //   log.info("oncefilter");
            String authorizationHeader = request.getHeader("Authorization");
            String refreshToken = request.getHeader("RefreshHeader");
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {

                    if (cookie.getName().equals("accessToken")) {
                        authorizationHeader = "Bearer " + cookie.getValue();

                        //  log.info("cookie.getValue()={}", authorizationHeader.substring(7));
                    }


                }
            }


            String username = null;
            String jwt = null;

            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {

                jwt = authorizationHeader.substring(7); //beaer 뒤에 붙은것들

                try {
                    username = jwtUtil.extractUsername(jwt); //extractUsername에서 유효기간이 지났다면 exception 발생
                } catch (ExpiredJwtException e) {
                    //   log.info("Error");

                    request.setAttribute("exception", "ExpiredJwtException");


                } catch (JwtException e) {
                    //          log.info("Error2");
                    e.printStackTrace();

                    request.setAttribute("exception", "JwtException");
                }

            }

//  1.AccessToken 유효성 체크

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                //2.토큰 사용자 조회

                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
                //      log.info("userDetails={}", userDetails);
                //      log.info("2");


                if (jwtUtil.validateToken(jwt, userDetails)) {


                    //userDetails.getAuthorities().forEach(r-> log.info("test={}",r));
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

}
