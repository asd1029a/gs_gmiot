package com.danusys.smartmetering.common.util;

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
	    	result = adminInfo.get("adminId").toString();
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
	    	result = adminInfo.get("adminName").toString();
	    }
		return result;
	}
	
	/**
	 * 로그인된 세션 회원 SQ
	 */
	public static String getSessionAdminSeq() throws Exception {
		String result = "";
		Map<?, ?> adminInfo = getSessionInfo();
		
	    if(adminInfo!=null) {
	    	result = adminInfo.get("adminSeq").toString();
	    }
		return result;
	}
	
	/**
	 * 로그인된 회원정보 세션값 
	 */
	public static Map<String, Object> getSessionInfo() throws Exception {
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
			hsr = sra.getRequest();
			sessionMap = (Map<String, Object>) hsr.getSession().getAttribute("adminInfo");
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