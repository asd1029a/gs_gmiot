package com.danusys.web.commons.auth.session.util;

import com.danusys.web.commons.app.StrUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public class SessionUtil {

	/**
	 * 로그인된 세션 아이디
	 */
	public static String getSessionAdminId() throws Exception {
		Map<?, ?> adminInfo = getSessionInfo();
	    String result = "NoAuth";
		
	    if(adminInfo!=null) {
	    	result = adminInfo.get("id").toString(); // adminId -> id로 변경
			System.out.println("sessionUtil에 로그인 세션 adminInfo : " + adminInfo);
	    }
		return result;
	}
	
	/**
	 * 로그인된 세션 아이디 이름
	 */
	public static String getSessionAdminName() throws Exception {
		Map<?, ?> adminInfo = getSessionInfo();
		String result = "";
		
	    if(adminInfo!=null) {

	    	result = adminInfo.get("userName").toString(); //adminName -> userName으로 변경
	    }
		return result;
	}
	
	/**
	 * 로그인된 세션 회원 SQ
	 */
	public static String getSessionAdminSeq() throws Exception {
		String result = "";

		Map<String, Object> adminInfo = getSessionInfo();
		Map<String,Object> adminMapInfo= adminInfo;

		System.out.println("sessionutil getsessionadminseq에서 adminInfo : " + adminInfo);
		System.out.println("!! : " + adminInfo );
		System.out.println("?? : " + adminMapInfo.get("userSeq"));

	    if(adminInfo!=null) {
	    	result = adminMapInfo.get("userSeq").toString();
	    }
		return result;
	}
	
	/**
	 * 로그인된 회원정보 세션값 
	 */
	public static Map<String, Object> getSessionInfo() throws Exception {
		System.out.println("getsessionInfo 들어옴 : sessionutil2입니다 ");
		/*
		if(SecurityContextHolder.getContext().getAuthentication()!=null) {
			UserInfo userInfo = (UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			
			result = userInfo.getUserId();
			System.out.println("=== userId : " + result);
		}
		*/
		ServletRequestAttributes sra = null;
		HttpServletRequest hsr = null;
		//AdminInfo adminInfo = null;
		Map<String, Object> sessionMap = null;

		try {
			sra = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
			System.out.println("1111" + sra);
			hsr = sra.getRequest();
			System.out.println("22222" + hsr);
			sessionMap = (Map<String, Object>) hsr.getSession().getAttribute("adminInfo");

			String result = StrUtils.getStr(sessionMap);


			System.out.println("hsr.getsession ::::: " + hsr.getSession());
			System.out.println("getAttribute::::::::::" + hsr.getSession().getAttribute("adminInfo"));
			System.out.println("3333result::: " + result);
//			ObjectMapper objectMapper = new ObjectMapper();
			//json string을 map으로 변환하기 ---> 이거 에러
//			sessionMap = objectMapper.readValue(result, new TypeReference<Map<String ,Object>>() {});


		} catch (Exception e) {
			/*if(SecurityContextHolder.getContext().getAuthentication()!=null) {
				if(!(SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof Object)) {
					sessionMap = (Map<String, Object>) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
				}
			}*/
		}

		return sessionMap;
	}
	
	/**
	 * 로그인된 회원정보 세션ID 
	 */
	public static String getSessionId() throws Exception {
		ServletRequestAttributes sra = null;
		HttpServletRequest hsr = null;
		String sessionId = "";
		
		sra = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
		hsr = sra.getRequest();
		sessionId = hsr.getSession().getId();
		
		return sessionId;
	} 
}