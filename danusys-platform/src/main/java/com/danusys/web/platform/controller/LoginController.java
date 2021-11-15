package com.danusys.web.platform.controller;

import com.danusys.web.platform.common.util.JsonUtil;
import com.danusys.web.platform.config.security.LoginDetailsService;
import com.danusys.web.platform.service.login.LoginService;
import com.danusys.web.platform.model.LoginVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * 통합 Platform 로그인
 */
@Controller
@RequestMapping("login")
public class LoginController {

	private LoginDetailsService loginDetailService;
	private LoginService loginService;

	@Autowired
	public LoginController(LoginDetailsService loginDetailService, LoginService loginService) {
		this.loginDetailService = loginDetailService;
		this.loginService = loginService;
	}

	@CrossOrigin
	@RequestMapping(value = "/checkSsoLoginId.do", produces = "application/json; charset=utf8", method = RequestMethod.POST)
	public void ssoLogin(@RequestBody Map<String, Object> param, HttpServletRequest request, HttpServletResponse response) {
		String ssoLoginId = param.get("ssoLoginId").toString();
		PrintWriter out;
		HashMap<String, Object> result = new HashMap<String, Object>();
		result.put("result", "N");
		      
		try {
			if (ssoLoginId != null) {
				LoginVO loginVO = (LoginVO) loginDetailService.loadUserByUsername(ssoLoginId);
				
				if (loginVO != null) {
					result.put("result", "Y");
				}
			}
		} catch(UsernameNotFoundException e) {
			e.printStackTrace();
		}
		
		try {
			out = response.getWriter();
			out.write(JsonUtil.MapToJson(result));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
 
	@RequestMapping(value = "/loginPage", method = RequestMethod.GET)
	public ModelAndView login(HttpServletRequest request) {
		ModelAndView mav = new ModelAndView();
		HttpSession session = request.getSession(false);
		LoginVO user = null;

		if (session != null) {
			user = (LoginVO) session.getAttribute("admin");
		}

		if (user == null) {
			mav.setViewName("view/login/login");
		} else {
			mav.setViewName("view/intro/intro");
		}

		return mav;
	}

	@RequestMapping(value = "/sessionExpired.do", method = RequestMethod.GET)
	public String sessionExpired(HttpServletRequest request, HttpServletResponse response) {
		return "login/sessionExpired";
	}

	@GetMapping("/logout")
	public String logout(HttpServletRequest request, HttpServletResponse response) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null) {
			new SecurityContextLogoutHandler().logout(request, response, authentication);
		}
		return "redirect:/login/loginPage";
	}

	@RequestMapping(value = { "/error.do" })
	public ModelAndView getLoginErrorPage(HttpServletRequest request, ModelMap model) {
		ModelAndView mav = new ModelAndView();

		mav.setViewName("login/loginError");

		return mav;
	}

	// 로그인페이지
	/*
	 * @RequestMapping(value = { "/loginPage.do" }) public ModelAndView
	 * getBetweenPage(HttpServletRequest request, ModelMap model) { ModelAndView mav
	 * = new ModelAndView(); try { LoginVO lgnVO = (LoginVO)
	 * request.getSession().getAttribute("admin");
	 * 
	 * LOGGER.info("===================  /loginPage.do 세션타임 >>>> {}",
	 * request.getSession().getMaxInactiveInterval());
	 * LOGGER.info("===================  /loginPage.do getUserId >>>> {}",
	 * request.getSession().getId());
	 * 
	 * if (lgnVO == null) { mav.setViewName("login"); } else {
	 * mav.addObject("admin", lgnVO); mav.setViewName("intro"); } } catch (Exception
	 * e) { LOGGER.error("Exception Message : {}", e.getMessage()); } return mav; }
	 */

	/**
	 * 로그인을 수행한다.
	 * 
	 * @param request
	 * @param id
	 * @param pwd
	 * @return
	 * @throws Exception
	 */
	/*
	 * @RequestMapping("/login.do") public String login(HttpServletRequest
	 * request, @RequestParam("id") String id, @RequestParam("pwd") String pwd) {
	 * LOGGER.info("===================  /login.do BEGIN ");
	 * 
	 * HttpSession session = request.getSession(); Map<String, Object> lgnParam =
	 * new HashMap<String, Object>();
	 * 
	 * String rtn = "redirect:/loginPage.do";
	 * 
	 * try { LoginVO lgnVO = this.loginService.login(request, id, pwd); if (lgnVO ==
	 * null) { return "redirect:/loginError.do"; }
	 * 
	 * LoginManager loginManager = LoginManager.getInstance(); if
	 * (!loginManager.isUsing(id)) { loginManager.setSession(session, id); String ip
	 * = request.getHeader("X-FORWARDED-FOR"); session.setAttribute("admin", lgnVO);
	 * session.setAttribute("ip", ip); // request.getSession().setAttribute("ip",
	 * ip); } else { rtn = "redirect:/loginError2.do?err=2"; }
	 * LOGGER.debug("===================  /login.do getUserCount >>>> {}",
	 * loginManager.getUserCount()); } catch (NullPointerException e) {
	 * request.getSession().removeAttribute("admin");
	 * request.getSession().invalidate();
	 * 
	 * LOGGER.debug("===================  /login.do NullPointerException >>>> {}",
	 * e.getMessage());
	 * 
	 * rtn = "redirect:/loginError.do"; } catch (Exception e) {
	 * request.getSession().removeAttribute("admin");
	 * request.getSession().invalidate();
	 * 
	 * LOGGER.debug("===================  /login.do Exception >>>> {}",
	 * e.getMessage());
	 * 
	 * rtn = "redirect:/loginError.do?err=2"; } return rtn; }
	 */

	/**
	 * 로그아웃을 수행한다.
	 * 
	 * @param request
	 * @param id
	 * @param pwd
	 * @return
	 * @throws Exception
	 */
	/*
	 * @RequestMapping("/logout.do") public String logout(HttpServletRequest
	 * request) { LOGGER.debug("세션 : ", request.getSession());
	 * 
	 * String rtn = "redirect:/loginPage.do"; HttpSession session =
	 * request.getSession();
	 * 
	 * LOGGER.debug("===================  /logout.do session >>>> {}", session);
	 * 
	 * LoginVO lgnVO = (LoginVO) session.getAttribute("admin");
	 * 
	 * try { InetAddress local = InetAddress.getLocalHost(); String ip =
	 * local.getHostAddress();
	 * 
	 * request.getSession().invalidate(); } catch (Exception e) {
	 * LOGGER.error("===================  /logout.do Exception >>>> {}",
	 * e.getMessage());
	 * 
	 * rtn = "redirect:/loginPage.do"; } return rtn; }
	 */

	/*
	 * @RequestMapping(value = { "/loginError.do" }) public ModelAndView
	 * getLoginErrorPage(HttpServletRequest request, ModelMap model) { ModelAndView
	 * mav = new ModelAndView();
	 * 
	 * try { LoginVO lgnVO = (LoginVO) request.getSession().getAttribute("admin");
	 * // ConfigVO configVo = (ConfigVO) configService.selectConfig();
	 * 
	 * LOGGER.debug("===================  /loginError.do 세션타임 >>>> {}",
	 * request.getSession().getMaxInactiveInterval());
	 * LOGGER.debug("===================  /loginError.do getUserId >>>> {}",
	 * request.getSession().getId());
	 * 
	 * if (lgnVO == null) { mav.setViewName("login/loginError"); } else {
	 * request.getSession().invalidate(); mav.setViewName("login"); } } catch
	 * (Exception e) { LOGGER.error("Exception Message : {}", e.getMessage()); }
	 * return mav; }
	 */

	/*
	 * @SuppressWarnings("unused")
	 * 
	 * @RequestMapping(value = { "/loginError2.do" }) public ModelAndView
	 * getLoginErrorPage2(HttpServletRequest request, ModelMap model) { String err =
	 * request.getParameter("err"); ModelAndView mav = new ModelAndView();
	 * 
	 * try {
	 * 
	 * LoginVO lgnVO = (LoginVO) request.getSession().getAttribute("admin");
	 * 
	 * LOGGER.debug("===================  /loginError.do 세션타임 >>>> {}",
	 * request.getSession().getMaxInactiveInterval());
	 * LOGGER.debug("===================  /loginError.do getUserId >>>> {}",
	 * request.getSession().getId());
	 * 
	 * if (lgnVO == null) { mav.setViewName("login/loginError2"); } else {
	 * request.getSession().invalidate(); mav.setViewName("login"); } } catch
	 * (Exception e) { LOGGER.error("Exception Message : {}", e.getMessage()); }
	 * return mav; }
	 */
}
