package com.danusys.smartmetering.common.util;

public class ExceptionUtil {
	
	public static void throwException(String str, boolean trace) throws Exception {
		Exception e = new Exception(str);
		if(!trace) {
			StackTraceElement[] stack = new StackTraceElement[0];
			e.setStackTrace(stack);
		}
		throw e;
	}
}