/*
package com.danusys.smartmetering.common.handler;

import com.danusys.smartmetering.admin.service.AdminService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class LoginFailureHandler extends SimpleUrlAuthenticationFailureHandler {

	protected final Log logger = LogFactory.getLog(getClass());

	@Autowired
	AdminService adminService;
	
	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
		try {
			logger.info("Login Fail");
			
			Map<String, Object> paramMap = new HashMap<String, Object>();
			String adminId = request.getParameter("username");
			paramMap.put("adminId", adminId);
			paramMap.put("loginType", 1);
			
			// 1. 로그인 이력 등록
			adminService.insertAdminLoginLog(paramMap);
			
			setDefaultFailureUrl("/admin/loginForm.do?error=1Z");
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		//request.getRequestDispatcher("/admin/loginForm.do?error="+errorCode).forward(request, response);
		super.onAuthenticationFailure(request, response, exception);
	}
}*/
