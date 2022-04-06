package com.danusys.web.smartmetering.dashboard.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class DashboardController {
	
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
	public String indexMain(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return "dashboard/index";
	}
}