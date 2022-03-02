package com.danusys.web.platform.test;

import com.danusys.web.commons.app.JsonUtil;
import com.danusys.web.platform.service.base.BaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Locale;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping(value = "/test")
public class TestController {
//	private static final Logger logger = LoggerFactory.getLogger(TestController.class);

	//	@Resource(name = "config")
//	private Properties config;
//	@Autowired
	private TestCommons testCommons;
	//	@Autowired
	private TestSocketClient socketClient;
	//	@Autowired
	private BaseService baseService;


	@Value("${danusys.event.link.ip}")
	private String targetIp;

	@Value("${danusys.event.link.port}")
	private int targetPort;

	@Autowired
	public TestController(TestCommons testCommons, TestSocketClient socketClient, BaseService baseService) {
		this.testCommons = testCommons;
		this.socketClient = socketClient;
		this.baseService = baseService;
	}

	@RequestMapping(value = "/testPage.do")
	public String page(HttpServletRequest request) {
		return "testPage/testPageMain";
	}

	@RequestMapping(value = "/sendEvent.do")
	public void test(HttpServletRequest request, HttpServletResponse response,
					 @RequestParam Map<String, Object> paramMap) throws Exception {
		request.setCharacterEncoding("UTF-8");
		PrintWriter out = null;

//		String targetIp = this.config.getProperty("event_link_ip");
//		int targetPort = Integer.parseInt(this.config.getProperty("event_link_port"));

		String result = "";
		try {
			result = socketClient.msgSend(testCommons.setBody(paramMap), this.targetIp, this.targetPort);
			out = response.getWriter();
			out.write(JsonUtil.OneStringToJson("SUCCESS"));
		} catch (Exception e) {
			e.printStackTrace();
			out.write(JsonUtil.OneStringToJson("FAIL"));
			log.debug("fail");
		}
	}

	/**
	 * FuncName : baseAction() FuncDesc : 페이지 Action Param : path : 지정경로 Return :
	 * String
	 */
	@RequestMapping(value = "/action/page", method = RequestMethod.POST)
	public ModelAndView baseAction(HttpServletRequest request, HttpServletResponse response, Locale locale,
								   Model model) {
		System.out.println("----------POST----------");
		System.out.println(request.getParameter("path"));

		ModelAndView mav = new ModelAndView();

		mav.setViewName(request.getParameter("path"));
		return mav;
	}
}


