package com.danusys.web.smartmetering.common.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class DateUtil {
	
	/**
	 * 현재 날짜 
	 * getCurrentDate
	 * @param format (yyyy-MM-dd HH:mm:ss)
	 * @return 날짜 String
	 */
	public static String getCurrentDate(String format) {
		Date now = new Date();
		DateFormat df = new SimpleDateFormat(format);
		String strDate = df.format(now);
		
		return strDate;
	}
	
	/**
	 * 현재 날짜 
	 * getCurrentDate
	 * @param format (yyyy-MM-dd HH:mm:ss)
	 * @return 날짜 String
	 */
	public static String getCurrentUtcDate(String format) {
		Date now = new Date();
		
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		
		return sdf.format(now);
	}
}
