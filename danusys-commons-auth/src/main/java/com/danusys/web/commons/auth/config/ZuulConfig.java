package com.danusys.web.commons.auth.config;


import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.danusys.web.commons.auth.config.auth.CommonsUserDetailsService;
import com.danusys.web.commons.auth.util.JwtUtil;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import groovy.util.logging.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.*;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Component
@Slf4j

public class ZuulConfig extends ZuulFilter {
    //@Autowired
    //private CommonsUserDetailsService userDetailsService;

    private final Logger log = LoggerFactory.getLogger(getClass());
    //
    @Autowired
    private JwtUtil jwtUtil;
    @Override
    public String filterType() {

       // return ROUTE_TYPE;
       return "pre";
       // return POST_TYPE;
    }

    @Override
    public int filterOrder() {
        return 1;
    }
//가지고만있으면
    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {
/*

            RequestContext ctx = RequestContext.getCurrentContext();
            HttpServletRequest request = ctx.getRequest();

            String url = request.getRequestURI();


            Cookie[] cookies = request.getCookies();
            //쿠키안에
            log.info("pre");
            ctx.addZuulRequestHeader("a","b");

            if (cookies != null) {
                log.info("hi2");
                for (Cookie cookie : cookies) {

                     //  log.info(cookie.getValue());
                    if (cookie.getName().equals("accessToken")) {
                        ctx.addZuulRequestHeader("Authorization", "Bearer " +cookie.getValue());
                    //    ctx.addZuulResponseHeader("Authorization", "Bearer " +cookie.getValue());

                    }

                    if (cookie.getName().equals("refreshToken")){
                        ctx.addZuulRequestHeader("RefreshHeader", cookie.getValue());
                     //     ctx.addZuulResponseHeader("RefreshHeader", cookie.getValue());
                    }



                }
            }


*/


        return null;
    }
}