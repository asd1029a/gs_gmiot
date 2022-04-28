package com.danusys.web.smartmetering.stats.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

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