/*
package com.danusys.smartmetering.common.handler;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
	
	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
		try {
			request.setAttribute("resultCode", "403");
			request.setAttribute("message", "요청 권한이 없습니다.");
			//ServletUtil.sendError(request, response);
			response.sendError(403, "요청 권한이 없습니다.");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}*/
