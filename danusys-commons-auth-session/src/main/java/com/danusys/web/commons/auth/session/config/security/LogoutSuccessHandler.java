/*
package com.danusys.web.commons.auth.config.security;


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

		System.out.println("로그아웃핸들러"+authentication.getDetails());

		if(authentication != null && authentication.getDetails() != null){
			try{
				request.getSession().invalidate();
			}catch (Exception e){
				e.printStackTrace();
			}
		}
		response.sendRedirect("/login");

		*/
/*String queryStr = "";

		if(request.getParameter("type")!=null) {
			queryStr = "?type="+request.getParameter("type");
		}

		setDefaultTargetUrl("/admin/loginForm.do"+queryStr);
		super.onLogoutSuccess(request, response, authentication);*//*

	}
}

*/
