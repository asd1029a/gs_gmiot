package com.danusys.smartmetering.common.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

public interface CommonService {
	public String getApiData(Map<String, Object> paramMap) throws Exception;
	public String getRemoteInfo(HttpServletRequest request) throws Exception;
	public Object exceptionProc(HttpServletRequest request) throws Exception;
	public Map<String,Object> getGeoJson(List<Map<String,Object>> geoList, String id) throws Exception;
}