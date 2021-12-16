package com.danusys.web.commons.auth.config.security;


import lombok.extern.slf4j.Slf4j;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Slf4j
public class AccessDeniedHandler implements org.springframework.security.web.access.AccessDeniedHandler {


	@Override
	public void handle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AccessDeniedException e) throws IOException, ServletException {

		httpServletRequest.setAttribute("failMsg", "권한이없습니다.");
		httpServletRequest.getRequestDispatcher("/login/error").forward(httpServletRequest,httpServletResponse);
	}
}
