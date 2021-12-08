package com.danusys.web.commons.auth.config.security;



import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Getter
@Setter
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

	private String defaultSuccessUrl;

	public LoginSuccessHandler(String defaultSuccessUrl) {
		this.defaultSuccessUrl = defaultSuccessUrl;
	}




	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {


		response.sendRedirect(defaultSuccessUrl);

	}




}
