package com.danusys.smartmetering.stats.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import com.danusys.smartmetering.admin.service.AdminService;
import com.danusys.smartmetering.common.util.StringUtil;
import com.danusys.smartmetering.gis.service.GisService;

@Controller
public class StatsController {
	Logger logger = LoggerFactory.getLogger(this.getClass());
	
	/**
	 * 통계 : 검침조회
	 */
	@RequestMapping(value = "/stats/accountStats.do")
	public String accountStats(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return "stats/accountStats";
	}
	
	/**
	 * 통계 : 이벤트조회
	 */
	@RequestMapping(value = "/stats/eventStats.do")
	public String eventStats(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return "stats/eventStats";
	}
}