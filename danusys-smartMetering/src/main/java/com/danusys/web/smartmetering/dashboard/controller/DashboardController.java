package com.danusys.web.smartmetering.dashboard.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.danusys.web.commons.auth.config.auth.CommonsUserDetails;
import com.danusys.web.smartmetering.admin.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class DashboardController {

	@Autowired
	AdminService adminService;

	/**
	 * 대시보드 메인 페이지
	 */
	@RequestMapping("/dashboard/main.do")
	public String dashboardMain(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return "dashboard/main";
	}

	/**
	 * 메뉴 페이지
	 */
	@RequestMapping("/dashboard/index.do")
	public String indexMain(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		// 로그인 후 업데이트 : UPDATE_ADMIN_AFTER_LOGIN
		Map<String, Object> paramMap = (Map<String, Object>) request.getSession().getAttribute("paramMap");
		adminService.updateAdminAfterLogin(paramMap);
		//로그인 이력 등록
		adminService.insertAdminLoginLog(paramMap);


		return "dashboard/index";
	}
}