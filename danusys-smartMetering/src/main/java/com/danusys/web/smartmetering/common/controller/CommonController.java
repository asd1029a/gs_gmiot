package com.danusys.web.smartmetering.common.controller;

import com.danusys.web.smartmetering.common.annotation.JsonRequestMapping;
import com.danusys.web.smartmetering.common.service.CommonService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Controller
@Slf4j
public class CommonController implements ErrorController {
	Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
    CommonService commonService;
	//@Autowired
	//ExcelUtil excelUtil;

	@Override
	public String getErrorPath() {
		return "redirect:/common/error.do";
	}


	/**
	 * 공통 : 인덱스 페이지
	 */
	@RequestMapping(value = "/")
	public String index(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		Object principal= SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		System.out.println("principal 정보 : " + principal);
		if(!principal.toString().equals("anonymousUser")) {
			return "redirect:/dashboard/index.do";
		}
		return "admin/loginForm";
	}

	/**
	 * 공통 : 에러 페이지
	 */
	@RequestMapping(value = "/common/error.do")
	public Object error(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return commonService.exceptionProc(request);
	}

	/**
	 * 공통 : 클라이언트 정보
	 */
	@RequestMapping(value = "/common/getRemoteInfo.ado", produces = "application/json; charset=utf8", method = RequestMethod.POST)
	public @ResponseBody String getRemoteInfo(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return commonService.getRemoteInfo(request);
	}

	/**
	 * 공통 : API 데이터 조회
	 */
	@JsonRequestMapping(value = "/common/getApiData.ado")
	public @ResponseBody String getApiData(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, Object> paramMap) throws Exception{
		return commonService.getApiData(paramMap);
	}


	/**
	 * 공통 : 엑셀 다운로드
	 */
	@RequestMapping(value = "/common/excelDownload.do")
	public ModelAndView excelDownload(ModelAndView mav, HttpServletRequest request, HttpServletResponse response, @RequestParam Map<String, Object> paramMap) throws Exception{
		return mav;
	}
}