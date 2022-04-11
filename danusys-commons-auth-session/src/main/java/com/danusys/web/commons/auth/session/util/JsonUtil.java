package com.danusys.web.commons.auth.session.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class JsonUtil {
	
	/**
	 * @param obj
	 * @return Web, Mobile에서 사용 - 커스텀 JSON 형태 
	 * @throws JsonProcessingException
	 */
	@SuppressWarnings("unchecked")
	public static String getJsonString(Object obj) throws JsonProcessingException {
		log.info("넘어온obj값 : " + obj);

		Map<String, Object> resultMap = new HashMap<String, Object>();
		ObjectMapper mapper = new ObjectMapper();
		String result = "";
		
		resultMap.put("resultCode", "000");
		
		//1. 500 에러
		if(obj instanceof Exception) {
			resultMap.put("resultCode", "500");
			resultMap.put("message", "처리중 오류가 발생하였습니다.");
		} else if (obj instanceof Map) {


			Map<String, Object> tMap = (Map<String, Object>) obj;
			
			if(tMap.containsKey("resultCode")) {
				resultMap = tMap;
			} else if(tMap.containsKey("pagingParam")) {
				resultMap.put("pagingParam", tMap.get("pagingParam"));
				resultMap.put("data", tMap.get("data"));
			} else {
				resultMap.put("data", tMap);
				if(!tMap.containsKey("message")) {
					resultMap.put("message", "Success");
				} else {
					resultMap.put("message", tMap.get("message"));
				}
			}
		} else {
			resultMap.put("data", obj);
			resultMap.put("message", "Success");
		}
		result = mapper.writeValueAsString(resultMap);

		log.info("obj : " + obj);
		log.info("jsonutil2에 resultmap : " + resultMap);
		log.info("jsonutil2에 result : " + result);
		return result;
	}
	
	/**
	 * @param OBJECT
	 * @return JSON Original Data Return 
	 * @throws JsonProcessingException
	 */
	public static String getOriJsonString(Object obj) throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(obj);
	}
	
	/**
	 * @param OBJECT
	 * @return JSON Original Data Return 
	 * @throws JsonProcessingException
	 */
	public static String getCntJsonString(int cnt) throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("cnt", cnt);
		return mapper.writeValueAsString(resultMap);
	}
}