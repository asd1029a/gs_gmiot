package com.danusys.web.commons.auth.config;


import com.danusys.web.commons.auth.util.JwtUtil;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component


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


            RequestContext ctx = RequestContext.getCurrentContext();
            HttpServletRequest request = ctx.getRequest();



            //쿠키안에
            log.info("pre");
            ctx.addZuulRequestHeader("a","b");

                log.info("hi2");



                        ctx.addZuulRequestHeader("Auth", "Bearer ");
                    //    ctx.addZuulResponseHeader("Authorization", "Bearer " +cookie.getValue());



                        ctx.addZuulRequestHeader("Refres123er","1");











        return null;
    }
}