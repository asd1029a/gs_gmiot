package com.danusys.smartmetering.common.util;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CustomUserDetails {
	
	public static String getJsonString(Object obj) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("data", obj);
		ObjectMapper mapper = new ObjectMapper();
		String result = "";
		try {
			result = mapper.writeValueAsString(obj);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return result;
	}
}
