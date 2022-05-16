package com.danusys.web.commons.app.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
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
        log.trace("createCookie 저장 완료 {} {} {}", cookieName, value, token_validation_second);
        return token;
    }

    public Cookie getCookie(HttpServletRequest req, String cookieName){
        final Cookie[] cookies = req.getCookies();
        if(cookies==null) return null;
        for(Cookie cookie : cookies){
            if(cookie.getName().equals(cookieName)) {
                log.trace("getCookie 조회 {}", cookieName);
                return cookie;
            }
        }
        return null;
    }

    public Cookie[] getCookies(HttpServletRequest req, String[] names){
        final Cookie[] cookies = req.getCookies();

        List<Cookie> list = new ArrayList<>();

        if(cookies==null) return null;

        for(Cookie cookie : cookies){
            Arrays.asList(names).stream().forEach(f -> {
                if(cookie.getName().equals(f)) {
                    log.trace("getCookie 조회 {}", f);
                    list.add(cookie);
                }
            });
        }

        return list.toArray(new Cookie[0]);
    }

}