
package com.danusys.web.commons.auth.session.config.security;

import com.danusys.web.commons.auth.session.config.auth.CommonsUserDetails;
import com.danusys.web.commons.auth.session.dto.response.UserResponse;
import com.danusys.web.commons.auth.session.model.User;
import com.danusys.web.commons.auth.session.repository.UserRepository;
import com.danusys.web.commons.auth.session.service.user.UserService;
import com.danusys.web.commons.auth.session.util.NetworkUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
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

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
										Authentication authentication) throws IOException, ServletException {

		log.info("loginSuccess Handler");

		CommonsUserDetails commonsUserDetails = null;
		Map<String, Object> adminMap = new HashMap<String, Object>();

		try {
			commonsUserDetails = (CommonsUserDetails) authentication.getPrincipal();

			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("adminId", commonsUserDetails.getUserId());
			paramMap.put("adminSeq", commonsUserDetails.getUserSeq());
			paramMap.put("loginType", 0);
			paramMap.put("requestIp", NetworkUtil.getLocalReqIp(request));
			request.getSession().setAttribute("adminId", paramMap.get("adminId"));

			// 1. 로그인후 업데이트
//            adminService2.updateAdminAfterLogin(paramMap);

			// 2. 로그인 이력 등록

//			adminMap = (Map<String, Object>) adminService.selectDetailAdmin(commonsUserDetails.getUserSeq());

			adminMap.put("adminSeq", commonsUserDetails.getUserSeq());
			adminMap.put("adminId", commonsUserDetails.getUserId());
			adminMap.put("password", commonsUserDetails.getPassword());

			request.getSession().setAttribute("adminInfo", adminMap);


			log.info("###adminMap : {} ", adminMap);




		}catch (Exception e){
			e.printStackTrace();
		}

		request.getSession(true).setAttribute("adminInfo", adminMap);

		response.sendRedirect(defaultSuccessUrl);

	}


}
