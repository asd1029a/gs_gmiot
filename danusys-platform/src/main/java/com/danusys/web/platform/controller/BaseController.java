package com.danusys.web.platform.controller;

import com.danusys.web.platform.model.LoginVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Slf4j
@Controller
public class BaseController {

	/*

	  date: 2022-01-18
	  수정내용 : return 값을 "view/index에서 view/login/test1로 수정함
	 */
	@RequestMapping(value = "/")
	public String index() {
		return "view/login/test1";
	}

//	/**
//	 * FuncName : mainPage() FuncDesc : 페이지 Move Param : Return : String
//	 */
//	@RequestMapping(value = "/main/main.do", method = { RequestMethod.GET, RequestMethod.POST, RequestMethod.HEAD })
//	public String mainPage(Locale locale, Model model, HttpServletRequest request, HttpServletResponse response,
//			HttpSession session) {
//		return "main/main";
//	}

	/**
	 * FuncName : baseAction() FuncDesc : 페이지 Action Param : path : 지정경로 Return :
	 * String
	 */
	@PostMapping("/action/page")
	public ModelAndView baseAction(HttpServletRequest request, HttpServletResponse response, Locale locale, Model model) {

		log.trace("# path : {}", request.getParameter("path"));

		HttpSession session = request.getSession();
		LoginVO lgnVO = (LoginVO) session.getAttribute("admin");

		ModelAndView mav = new ModelAndView();

		if (lgnVO != null) {
			log.trace("# lgnVO {}", lgnVO.toString());

			String adminId = lgnVO.getId();
			String adminNm = lgnVO.getName();

			InetAddress local = null;
			try {
				local = InetAddress.getLocalHost();
			} catch (UnknownHostException e1) {
				e1.printStackTrace();
			}

			Map<String, Object> param = new HashMap<String, Object>();
			String ip = local.getHostAddress();

			param.put("sessionId", request.getSession().getId());
			param.put("adminId", adminId);
			param.put("adminNm", adminNm);
			param.put("ip", ip);
			param.put("logType", "ACCESSPAGE");
			param.put("content", request.getParameter("path"));

//			try {
//				baseService.baseInsert("log.insertLog", param);
//			} catch (Exception ex) {
//				log.error(ex.toString());
//			}

			Enumeration params = request.getParameterNames();
			while (params.hasMoreElements()) {
				String name = (String) params.nextElement();
				mav.addObject(name, request.getParameter(name));
			}
			mav.setViewName("view/" + request.getParameter("path"));
		} else {
			log.trace("# lgnVO 세션 없음");
			mav.addObject("message", "세션이 종료되어 로그아웃 되었습니다.");
			mav.addObject("sessionId", "sessionOut");
			mav.setViewName("view/login/login");
		}
		return mav;
	}

	/**
	 * FuncName : baseActionGet() FuncDesc : 페이지 Action Param : path : 지정경로 Return :
	 * String
	 */
	@GetMapping("/action/page")
	public ModelAndView baseActionGet(HttpServletRequest request, Locale locale, Model model) {
		log.trace("----------GET----------");
		log.trace("# path : {}", request.getParameter("path"));
		ModelAndView mav = new ModelAndView();
		mav.setViewName("view/" + request.getParameter("path"));
		mav.addObject("serverName",request.getServerName());
		return mav;
	}

//	/**
//	 * FuncName : baseInsert() FuncDesc : 등록 Param : sqlid : SQL ID Return : String
//	 */
//	@RequestMapping(value = "/insert/{sqlid}/action.do", method = RequestMethod.POST)
//	public String baseInsert(@PathVariable("sqlid") String sqlid, HttpServletRequest request,
//			HttpServletResponse response, Locale locale, Model model) throws IOException {
//
//		int iResult = 0;
//		Map<String, Object> param = JsonUtil.JsonToMap(request.getParameter("param"));
//
//		try {
//			// Insert
//				iResult = baseService.baseInsert(sqlid, param);
//			if (iResult == 1) {
//				model.addAttribute("result", "SUCCESS");
//				model.addAttribute("path", request.getParameter("path"));
//				model.addAttribute("action", request.getParameter("action"));
//				model.addAttribute("message", "정상적으로 '등록' 되었습니다.");
//			}
//		} catch (Exception ex) {
//			log.error(ex.toString());
//		}
//		return "/views/result/result";
//	}
	
	  
//    @RequestMapping("/input/form.do")
//	public String inputForm(@RequestParam Map<String,Object> param) throws Exception {
//
//    	String path = (String) param.get("path");
//    	String sqlId = (String) param.get("sqlId");
//    	baseService.baseInsert(sqlId,param);
//
//		return path;
//    }

//	/**
//	 * FuncName : baseUpdate() FuncDesc : 수정 Param : sqlid : SQL ID Return : String
//	 */
//	@RequestMapping(value = "/update/{sqlid}/action.do", method = RequestMethod.POST)
//	public String baseUpdate(@PathVariable("sqlid") String sqlid, HttpServletRequest request, Locale locale,
//			Model model) throws IOException {
//
//		int iResult = 0;
//		Map<String, Object> param = JsonUtil.JsonToMap(request.getParameter("param"));
//
//		try {
//			// Update
//			iResult = baseService.baseUpdate(sqlid, param);
//
//			if (iResult == 1) {
//				model.addAttribute("result", "SUCCESS");
//				model.addAttribute("path", request.getParameter("path"));
//				model.addAttribute("action", request.getParameter("action"));
//				model.addAttribute("message", "정상적으로 '수정' 되었습니다.");
//			}
//		} catch (Exception ex) {
//			log.error(ex.toString());
//		}
//		return "/views/result/result";
//	}

//	/**
//	 * FuncName : baseDelete() FuncDesc : 삭제 Param : sqlid : SQL ID Return : String
//	 */
//	@RequestMapping(value = "/delete/{sqlid}/action.do", method = RequestMethod.POST)
//	public @ResponseBody Map<String, Object> baseDelete(@PathVariable("sqlid") String sqlid, HttpServletRequest request,
//			Locale locale, Model model) throws IOException {
//
//		int iResult = 0;
//		Map<String, Object> param = JsonUtil.JsonToMap(request.getParameter("param"));
//
//		try {
//			// Delete
//			iResult = baseService.baseDelete(sqlid, param);
//			System.out.println("DELETE iResult = [" + iResult + "]");
//
//			if (iResult > -1) {
//				model.addAttribute("result", "SUCCESS");
//				model.addAttribute("path", request.getParameter("path"));
//				model.addAttribute("action", request.getParameter("action"));
//				model.addAttribute("message", "정상적으로 '삭제' 되었습니다.");
//			}
//		} catch (Exception ex) {
//			log.error(ex.toString());
//		}
//		return param;
//	}

//	@RequestMapping(value = "/select/selectCctvPreset.do", method = RequestMethod.POST)
//	public String selectCctvPreset(HttpServletRequest request, HttpServletResponse response, Model model) throws IOException, ModelAndViewDefiningException {
//		PrintWriter out = null;
//		response.setCharacterEncoding("UTF-8");
//		response.setHeader("Access-Control-Allow-Origin", "*"); // 크로스도메인 허용
//
//		Map<String, Object> param = null;
//		if (request.getParameter("param").trim().equals("") == true) {
//			param = new HashMap<String, Object>();
//		} else {
//			param = JsonUtil.JsonToMap(request.getParameter("param"));
//		}
//
//		List<Map<String, Object>> resList = new ArrayList<Map<String, Object>>();
//		try {
//			resList = baseService.baseSelectList("facility.selectCctvPreset", param);
//
//			model.addAttribute("resList", resList);
//
//			out = response.getWriter();
//			out.write(JsonUtil.ListToJson(resList)); // Ajax Retun Json String
//		} catch (Exception ex) {
//			log.error(ex.toString());
//		}
//		return request.getParameter("path");
//	}
}
