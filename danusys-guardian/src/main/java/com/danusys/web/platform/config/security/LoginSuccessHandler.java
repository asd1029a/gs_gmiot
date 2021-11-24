package com.danusys.web.platform.config.security;

import com.danusys.web.platform.model.LoginVO;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Slf4j
@Getter
@Setter
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

	private String defaultSuccessUrl;

	public LoginSuccessHandler(String defaultSuccessUrl) {
		this.defaultSuccessUrl = defaultSuccessUrl;
	}

	@Autowired
	private LoginDetailsService loginDetailService;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {

		log.trace("# login Success");
		clearAuthenticationAttributes(request);

		String path = request.getRequestURI();
		String queryString = request.getQueryString();

		HttpSession session = request.getSession();
		
		LoginVO loginVO = null;
		Object principal = authentication.getPrincipal();
		
		if(principal instanceof LoginVO) {
			loginVO = (LoginVO) principal;
		} else if(principal instanceof String) {
			loginVO = (LoginVO) loginDetailService.loadUserByUsername(authentication.getPrincipal().toString());
		}
		
		loginVO.setSessionId(session.getId());

		session.setAttribute("id", loginVO.getUsername());
		session.setAttribute("pwd", loginVO.getPassword());
		session.setAttribute("admin", loginVO);
		session.setAttribute("clientIp", getClientIp(request));
		session.setAttribute("serverName", request.getServerName());

		response.setStatus(HttpServletResponse.SC_OK);

		log.trace("# loginVO    : {}", loginVO.toString() );
		log.trace("# admin Attr : {}", session.getAttribute("admin") );

		if(path.contains("action")) {
			response.sendRedirect(path + "?" + queryString);
		} else {
			response.sendRedirect(defaultSuccessUrl);
		}
	}

	protected void clearAuthenticationAttributes(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if(session==null) return;
        session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
	}
	
	private String getClientIp(HttpServletRequest request) {
		String ip = request.getHeader("X-FORWARDED-FOR");
		if (ip == null)
			ip = request.getRemoteAddr();
		
		return ip;
	}
}
