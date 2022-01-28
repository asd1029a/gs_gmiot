package com.danusys.web.commons.auth.filter;


import com.danusys.web.commons.auth.config.auth.CommonsUserDetailsService;
import com.danusys.web.commons.auth.util.JwtUtil;
import com.netflix.zuul.context.RequestContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@Slf4j


public class BeforeJwtRequestFilter extends OncePerRequestFilter {
    //public class JwtRequestFilter extends OncePerRequestFilter {


    @Autowired
    private CommonsUserDetailsService userDetailsService;


    @Autowired
    private JwtUtil jwtUtil;

    @Bean
    public FilterRegistrationBean filterRegistrationBean() {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        registrationBean.setFilter(new BeforeJwtRequestFilter());
        registrationBean.setEnabled(false);
        return registrationBean;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        RequestContext ctx = RequestContext.getCurrentContext();
      //  request = ctx.getRequest();

     //   String url = request.getRequestURI();


        Cookie[] cookies = request.getCookies();
        //쿠키안에
        log.info("pre");
        ctx.addZuulRequestHeader("a","b");

        if (cookies != null) {
            log.info("hi2");
            for (Cookie cookie : cookies) {

                //  log.info(cookie.getValue());
                if (cookie.getName().equals("accessToken")) {
                   // ctx.addZuulRequestHeader("Authorization", "Bearer " +cookie.getValue());
                    //    ctx.addZuulResponseHeader("Authorization", "Bearer " +cookie.getValue());


                }

                if (cookie.getName().equals("refreshToken")){
                    ctx.addZuulRequestHeader("RefreshHeader", cookie.getValue());
                    //     ctx.addZuulResponseHeader("RefreshHeader", cookie.getValue());

                }



            }
        }





        chain.doFilter(request, response);
    }

}
