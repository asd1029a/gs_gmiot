package com.danusys.web.smartmetering.common.util;

import java.security.SecureRandom;

public class StringUtil {
	
	/**
	 * 숫자, 소문자를 이용한 난수 생성
	 * @param length
	 * @return
	 */
	public static String getRandomAlpha(int length){
		
		String strRange = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		SecureRandom rnd = new SecureRandom();		
				
	   StringBuilder sb = new StringBuilder(length);
	   for( int i = 0; i < length; i++ ) {
	      sb.append( strRange.charAt( rnd.nextInt(strRange.length()) ) );
	   }
	   return sb.toString();
	}	

	/**
	 * 숫자, 소문자를 이용한 난수 생성
	 * @param length
	 * @return
	 */
	public static String getRandomString(int length){
		
		String strRange = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		SecureRandom rnd = new SecureRandom();		
				
	   StringBuilder sb = new StringBuilder(length);
	   for( int i = 0; i < length; i++ ) {
	      sb.append( strRange.charAt( rnd.nextInt(strRange.length()) ) );
	   }
	   return sb.toString();
	}	
	
	/**
	 * 숫자를 이용한 난수 생성
	 * @param length
	 * @return
	 */
	public static String getRandomNumber(int length) {
		String strRange = "0123456789";
		SecureRandom rnd = new SecureRandom();		
				
		StringBuilder sb = new StringBuilder(length);
		for( int i = 0; i < length; i++ ) {
			sb.append( strRange.charAt( rnd.nextInt(strRange.length()) ) );
		}
		return sb.toString();
	}
	
	/**
	 * @return tiles Definition
	 */
	public static String convertUrl(String tilesDef, String subUrl) {
		return (subUrl==null ? "" : subUrl + "/") + tilesDef;
	}
}