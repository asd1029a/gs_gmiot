package com.danusys.web.smartmetering.event.service;

import java.util.Map;

public interface EventService {

	public String selectListEvent(Map<String, Object> paramMap) throws Exception;
	public String selectListEventForTotalPerChart(Map<String, Object> paramMap) throws Exception;
	public int insertEventLog(Map<String, Object> paramMap) throws Exception;
	public int updateEventLog(Map<String, Object> paramMap) throws Exception;
	public String selectListEventGeojson(Map<String, Object> paramMap) throws Exception;
	public String selectListEventGIS(Map<String, Object> paramMap) throws Exception;
	public String selectListLastestEventByAccount(Map<String, Object> paramMap) throws Exception;
	public String selectListEventLog(Map<String, Object> paramMap) throws Exception;
	public String selectListEventDataStats(Map<String, Object> paramMap) throws Exception;
	public String updateEventStep(Map<String, Object> paramMap) throws Exception;
	public String selectDetailEventTotalCnt(Map<String, Object> paramMap) throws Exception;
	public String selectListEventForProcessPerChart(Map<String, Object> paramMap) throws Exception;
	public String selectListEventForStatsChart(Map<String, Object> paramMap) throws Exception;
}