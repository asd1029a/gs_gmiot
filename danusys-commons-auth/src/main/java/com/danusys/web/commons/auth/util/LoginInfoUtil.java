package com.danusys.web.commons.auth.util;

import com.danusys.web.commons.auth.config.auth.CommonsUserDetails;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class LoginInfoUtil {

    public static CommonsUserDetails getUserDetails()
    {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        CommonsUserDetails userDetails = (CommonsUserDetails) principal;
        return userDetails;
    }

}
