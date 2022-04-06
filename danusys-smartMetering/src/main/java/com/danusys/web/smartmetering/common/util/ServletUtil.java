/*
package com.danusys.smartmetering.common.util;

import javax.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class ServletUtil {
	ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
	HttpServletRequest hsr = sra.getRequest();
	
	public static HttpServletRequest getRequest() {
		ServletRequestAttributes sra = null;
		HttpServletRequest hsr = null;
		
		try {
			sra = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
			hsr = sra.getRequest();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return hsr;
	}
}*/
