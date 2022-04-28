package com.danusys.web.smartmetering.gis.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import com.danusys.web.smartmetering.gis.service.GisService;

@Controller
public class GisController {
	Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	GisService adminService;
	
	/**
	 * GIS : 메인 페이지
	 */
	@RequestMapping(value = "/gis/main.do")
	public String main(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return "gis/main";
	}
	
	//샘플
//	@RequestMapping(value = "/common/getRemoteInfo.ado", produces = "application/json; charset=utf8", method = RequestMethod.POST)
//	public @ResponseBody String getRemoteInfo(HttpServletRequest request, HttpServletResponse response) throws Exception {
//		return commonService.getRemoteInfo(request);
//	}
}