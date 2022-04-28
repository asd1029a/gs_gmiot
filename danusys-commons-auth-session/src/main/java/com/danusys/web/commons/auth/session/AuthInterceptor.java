/*
package com.danusys.web.commons.auth.session.interceptor;

import com.danusys.web.commons.auth.session.util.SessionUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
@Slf4j

public class AuthInterceptor extends HandlerInterceptorAdapter {

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		log.info("#####AuthInterceptor Prehandle");

		String uri = request.getRequestURI();
		Map<?, ?> adminInfo = SessionUtil.getSessionInfo();
		boolean isAjax = false;
		
		if(uri.indexOf(".ado")>-1) {
			isAjax = true;
		}
		
		if(adminInfo==null) {
			if(isAjax) {
				response.sendError(403);
			} else {
				response.sendRedirect("/admin/loginForm.do");
			}
		}

		log.info("####AuthInterceptor isAjax? : " + isAjax);

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
