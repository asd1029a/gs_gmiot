/*
package com.danusys.smartmetering.common.handler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class LogoutSuccessHandler extends SimpleUrlLogoutSuccessHandler {

	protected final Log logger = LogFactory.getLog(getClass());
	
	@Override
	public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
			throws IOException, ServletException {
		
		String queryStr = "";
		
		if(request.getParameter("type")!=null) {
			queryStr = "?type="+request.getParameter("type");
		}
		
		setDefaultTargetUrl("/admin/loginForm.do"+queryStr);
		super.onLogoutSuccess(request, response, authentication);
	}
}*/
