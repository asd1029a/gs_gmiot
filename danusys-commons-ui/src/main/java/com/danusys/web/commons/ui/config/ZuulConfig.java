package com.danusys.web.commons.ui.config;


import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import groovy.util.logging.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.*;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Component
@Slf4j
public class ZuulConfig extends ZuulFilter {


    //
//    @Autowired
//    private JwtUtil jwtUtil;
    @Override
    public String filterType() {

        return ROUTE_TYPE;
       // return PRE_TYPE;
        //return POST_TYPE;
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() throws ZuulException {



        try {
            final RequestContext ctx = RequestContext.getCurrentContext();
            final HttpServletRequest request = ctx.getRequest();
            String refreshToken = null;
            Cookie[] cookies = request.getCookies();

            String accessTokenValue;
            if (cookies != null) {
                for (Cookie cookie : cookies) {

                    //   log.info(cookie.getValue());
                    if (cookie.getName().equals("accessToken")) {
                        ctx.addZuulRequestHeader("Authorization", "Bearer " +cookie.getValue());
                        System.out.println("1");
                        //log.info("cookie.getValue()={}",authorizationHeader.substring(7));
                    }

                    if (cookie.getName().equals("refreshToken"))
                        System.out.println("2");
                        ctx.addZuulRequestHeader("RefreshHeader", cookie.getValue());


                }
            }


        } catch (Exception e) {
            throw new ZuulException(e, INTERNAL_SERVER_ERROR.value(), e.getMessage());
        }

        return null;
    }
}