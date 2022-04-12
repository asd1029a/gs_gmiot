/*
package com.danusys.web.commons.auth.session.interceptor;

import com.danusys.web.commons.auth.session.util.SessionUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.regex.Pattern;

@Slf4j

public class CommonInterceptor extends HandlerInterceptorAdapter {

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		log.info("#####CommonInterceptor Prehandle");
		MDC.put("sessionId", SessionUtil.getSessionAdminId());
		
		String uri = request.getRequestURI();
		String regex = "^[A-Za-z0-9_/.]+(do|ado)$";
		
		if(Pattern.matches(regex, uri)) {
			//logger.info(request.getRequestURI());
		}

		log.info("####CommonInterceptor MDC? : " + MDC.get("sessionId"));
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
		super.postHandle(request, response, handler, modelAndView);
	}
	
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
		super.afterCompletion(request, response, handler, ex);
	}
}*/
