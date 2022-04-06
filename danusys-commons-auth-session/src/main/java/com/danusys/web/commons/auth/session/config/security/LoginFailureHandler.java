
package com.danusys.web.commons.auth.session.config.security;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Setter
@Getter
public class LoginFailureHandler implements AuthenticationFailureHandler {

	private String defaultFailureUrl;

	public LoginFailureHandler(String defaultFailureUrl) {
		this.defaultFailureUrl = defaultFailureUrl;
	}

	@Override
	public void onAuthenticationFailure(HttpServletRequest request,
										HttpServletResponse response, AuthenticationException authenticationException) throws IOException, ServletException {

		String defaultFailureUrl = this.defaultFailureUrl;

		if(authenticationException instanceof AuthenticationServiceException) {
			log.error("AuthenticationServiceException");
			request.setAttribute("failMsg", "가입되지 않은 사용자");
		} else if(authenticationException instanceof BadCredentialsException) {
			log.error("BadCredentialsException");
			request.setAttribute("failMsg", "아이디 또는 비밀번호가 틀렸습니다.");
		} else if(authenticationException instanceof LockedException) {
			log.error("LockedException");
			request.setAttribute("failMsg", "계정이 잠겨있습니다. 관리자에게 문의하십시오.");
		} else if(authenticationException instanceof DisabledException) {
			log.error("DisabledException");
			request.setAttribute("failMsg", "비황성화된 계정입니다.");
		} else if(authenticationException instanceof AccountExpiredException) {
			log.error("AccountExpiredException");
			request.setAttribute("failMsg", "기간 만료된 계정입니다.");
		} else if(authenticationException instanceof CredentialsExpiredException) {
			log.error("CredentialsExpiredException");
			request.setAttribute("failMsg", "비밀번호 틀렸습니다.");
		}

		log.trace("# defaultFailureUrl : {} ", defaultFailureUrl);

		response.sendRedirect(defaultFailureUrl);
	}
}
