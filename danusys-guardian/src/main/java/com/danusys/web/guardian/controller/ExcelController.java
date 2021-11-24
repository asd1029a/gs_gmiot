package com.danusys.web.guardian.controller;

import com.danusys.web.guardian.model.LoginVO;
import com.danusys.web.guardian.service.file.ExcelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@Controller
public class ExcelController {
	private ExcelService excelService;

	@Autowired
	public ExcelController(ExcelService excelService) {
		this.excelService = excelService;
	}
 	/*
	 * 엑셀다운로드 
	 */
	@PostMapping("/excelDownload/{sqlid}/action")
	public ModelAndView excelDownload(@PathVariable("sqlid") String sqlid, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		response.setCharacterEncoding("UTF-8");
		response.setHeader("Access-Control-Allow-Origin", "*"); // 크로스도메인 허용
		HttpSession session = request.getSession(false);
		LoginVO user = (LoginVO) session.getAttribute("admin");

		Map<String, Object> map = new HashMap<String, Object>();

		if (user != null) {
			map = excelService.excelDownLoad(sqlid, request);
		} else {
			map.put("rows", "sessionOut");
		}
		return new ModelAndView("viewExcel", map);
	}
}