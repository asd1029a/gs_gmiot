package com.danusys.smartmetering.common.util;

import javax.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class NetworkUtil {

	/**
	 * 접속 클라이언트 IP
	 * @param HttpServletRequest
	 * @return XXX.XXX.XXX.XXX 문자열
	 */
	public static String getLocalReqIp(HttpServletRequest request) throws Exception {
		String ip = request.getHeader("x-Forwarded-For");

		if ((ip == null) || (ip.length() == 0) || ("unknown".equalsIgnoreCase(ip))) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if ((ip == null) || (ip.length() == 0) || ("unknown".equalsIgnoreCase(ip))) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if ((ip == null) || (ip.length() == 0) || ("unknown".equalsIgnoreCase(ip))) {
			ip = request.getHeader("HTTP_CLIENT_IP");
		}
		if ((ip == null) || (ip.length() == 0) || ("unknown".equalsIgnoreCase(ip))) {
			ip = request.getHeader("HTTP_X_FORWARDED_FOR");
		}
		if ((ip == null) || (ip.length() == 0) || ("unknown".equalsIgnoreCase(ip))) {
			ip = request.getRemoteAddr();
		}
		if ((ip == null) || (ip.length() == 0) || ("unknown".equalsIgnoreCase(ip))) {
			ip = "";
		}
		
		if("0:0:0:0:0:0:0:1".equals(ip)) {
			ip = "localhost";
		}
		return ip;
	}
	
	/**
	 * 접속 클라이언트 IP
	 * @return XXX.XXX.XXX.XXX 문자열
	 */
	public static String getLocalReqIp() throws Exception {
		ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
	    HttpServletRequest hsr = sra.getRequest();
		return getLocalReqIp(hsr);
	}
}