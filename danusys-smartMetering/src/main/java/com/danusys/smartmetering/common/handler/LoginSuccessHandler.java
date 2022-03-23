/*package com.danusys.smartmetering.common.handler;

import com.danusys.smartmetering.admin.service.AdminService;
import com.danusys.smartmetering.common.model.AdminInfo;
import com.danusys.smartmetering.common.util.NetworkUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

	protected final Log logger = LogFactory.getLog(getClass());
	
	@Autowired
	AdminService adminService;
	
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
		AdminInfo adminInfo = null;
		Map<String, Object> adminMap = null;
		
		try {
			adminInfo = (AdminInfo) authentication.getPrincipal();
			
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("adminId", adminInfo.getAdminId());
			paramMap.put("adminSeq", adminInfo.getAdminSeq());
			paramMap.put("loginType", 0);
			paramMap.put("requestIp", NetworkUtil.getLocalReqIp(request));
			
			// 1. 로그인후 업데이트
			adminService.updateAdminAfterLogin(paramMap);
			
			// 2. 로그인 이력 등록
			adminService.insertAdminLoginLog(paramMap);
			
			// 3. 세션 저장
			adminMap = (Map<String, Object>) adminService.selectDetailAdmin(adminInfo.getAdminSeq());
			request.getSession().setAttribute("adminInfo", adminMap);
			
			setDefaultTargetUrl("/");
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		request.getSession(true).setAttribute("adminInfo", adminMap);
		
		super.handle(request, response, authentication);
		super.clearAuthenticationAttributes(request);
	}
}*/