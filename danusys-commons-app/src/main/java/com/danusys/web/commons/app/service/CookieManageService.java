package com.danusys.web.commons.app.service;

import com.danusys.web.commons.app.model.CookieData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Project : danusys-webservice-parent
 * Created by Intellij IDEA
 * Developer : ippo
 * Date : 2022/06/03
 * Time : 14:36
 */
@Slf4j
@Service
public class CookieManageService {
    private Map<String, CookieData> cookieDataMap = new HashMap<>();

    public Cookie getCookie(String cookieName) {
        return this.checkExpired(cookieName);
    }

    private CookieData getCookieData(String cookieName) {
        return cookieDataMap.get(cookieName);
    }

    private Cookie checkExpired(String cookieName) {
        CookieData cookieData = this.getCookieData(cookieName);
        if (cookieData== null) {
            return null;
        } else if (isExpired(cookieData)) {
            cookieDataMap.remove(cookieName);
            return null;
        } else {
            return cookieData.getCookie();
        }
    }

    private boolean isExpired(CookieData cookieData) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiredTime = cookieData.getExpiredTime();
        if (now.isEqual(expiredTime) || now.isAfter(expiredTime)) {
            return true;
        }

        return false;
    }

    public Cookie createCookie(String cookieName, String value) {
        return createCookie(cookieName, value, false, false, 1 * 60);
    }

    public Cookie createCookie(String cookieName, String value, int tokenValidationSecond) {
        return createCookie(cookieName, value, false, false, tokenValidationSecond);
    }

    public Cookie createCookie(String cookieName, String value, boolean httpOnly, boolean secure, int tokenValidationSecond){
        LocalDateTime createTime = LocalDateTime.now();
        LocalDateTime expiredTime = createTime.plusSeconds(tokenValidationSecond);
        Cookie token = new Cookie(cookieName,value);
        token.setHttpOnly(httpOnly);
        token.setSecure(secure);
        token.setMaxAge(tokenValidationSecond);
        token.setPath("/");
        log.trace("createCookie 저장 완료 {} {} {}", cookieName, value, tokenValidationSecond);
        CookieData cookieData = CookieData.builder().cookie(token).createTime(createTime).expiredTime(expiredTime).build();
        cookieDataMap.put(cookieName, cookieData);
        return token;
    }
}
