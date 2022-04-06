
package com.danusys.web.commons.auth.session.config.security;

import com.danusys.web.commons.auth.session.config.auth.CommonsUserDetails;
import com.danusys.web.commons.auth.session.service.AdminService;
import com.danusys.web.commons.auth.session.util.NetworkUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Getter
@Setter
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

	private String defaultSuccessUrl;

	public LoginSuccessHandler(String defaultSuccessUrl) throws Exception {
		this.defaultSuccessUrl = defaultSuccessUrl;
	}

	protected final Log logger = LogFactory.getLog(getClass());

	@Autowired
	AdminService adminService;



	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
										Authentication authentication) throws IOException, ServletException {

		log.info("loginSuccess Handler");

		CommonsUserDetails commonsUserDetails = null;
		Map<String, Object> adminMap = null;

		try {
			commonsUserDetails = (CommonsUserDetails) authentication.getPrincipal();

			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("adminId", commonsUserDetails.getUserId());
			paramMap.put("adminSeq", commonsUserDetails.getUserSeq());
			paramMap.put("loginType", 0);
			paramMap.put("requestIp", NetworkUtil.getLocalReqIp(request));

			log.info("파람맵 : " + paramMap);

			// 1. 로그인후 업데이트
//            adminService2.updateAdminAfterLogin(paramMap);

			// 2. 로그인 이력 등록
			adminMap = (Map<String, Object>) adminService.selectDetailAdmin(commonsUserDetails.getUserSeq());

			log.info("adminmap1111 : " + adminMap);

			request.getSession().setAttribute("adminInfo", adminMap);

		}catch (Exception e){
			e.printStackTrace();
		}

		request.getSession(true).setAttribute("adminInfo", adminMap);

		response.sendRedirect(defaultSuccessUrl);

	}
/*

	final HttpSession session = request.getSession();

		Map<String, Object> adminMap = null;

		log.trace("# login Success");


		clearAuthenticationAttributes(request);

		String path = request.getRequestURI();
		String queryString = request.getQueryString();

		CommonsUserDetails loginVO = null;
		Object principal = authentication.getPrincipal();

		log.info(authentication.getPrincipal().toString());

		loginVO=(CommonsUserDetails) authentication.getPrincipal(); //다누시스

		log.info(loginVO.getAdminId()+"/"+loginVO.getUserId()+"/"+loginVO.getUserSeq());

		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("adminId", loginVO.getUserId());
		paramMap.put("adminSeq", loginVO.getUserSeq());
		paramMap.put("loginType", 0);
		try{
			paramMap.put("requestIp", NetworkUtil2.getLocalReqIp(request));

			// 1. 로그인후 업데이트
			adminService2.updateAdminAfterLogin(paramMap);

			// 2. 로그인 이력 등록
			adminService2.insertAdminLoginLog(paramMap);

		}catch (Exception e){
			e.printStackTrace();
		}


		if(principal instanceof UserRequest) {
			loginVO = (CommonsUserDetails) principal;
		} else if(principal instanceof String) {
			loginVO = (CommonsUserDetails) commonsUserDetailsService.loadUserByUsername(authentication.getPrincipal().toString());
		}


//		loginVO.setSessionId(session.getId());


		// 3. 세션 저장
		try {
			adminMap = (Map<String, Object>) adminService2.selectDetailAdmin(loginVO.getAdminSeq());
		} catch (Exception e) {
			e.printStackTrace();
		}
		request.getSession().setAttribute("adminInfo", adminMap);



		session.setAttribute("id", loginVO.getUserId());
		session.setAttribute("pwd", loginVO.getPassword());
		session.setAttribute("admin", loginVO);
		session.setAttribute("clientIp", getClientIp(request));
		session.setAttribute("serverName", request.getServerName());
		log.info("Session={}",session.getAttribute("id"));
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
*/

}
