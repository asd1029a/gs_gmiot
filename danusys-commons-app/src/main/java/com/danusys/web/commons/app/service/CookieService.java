package com.danusys.web.commons.app.service;

import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

@Service
public class CookieService {

    public Cookie createCookie(HttpServletRequest req, String cookieName, String value, int token_validation_second){
        Cookie token = new Cookie(cookieName,value);
        if (req.isSecure()) {
            token.setHttpOnly(true);
            token.setSecure(true);
        }
        token.setMaxAge(token_validation_second);
        token.setPath("/");
        return token;
    }

    public Cookie getCookie(HttpServletRequest req, String cookieName){
        final Cookie[] cookies = req.getCookies();
        if(cookies==null) return null;
        for(Cookie cookie : cookies){
            if(cookie.getName().equals(cookieName))
                return cookie;
        }
        return null;
    }

}