package com.danusys.web.platform.service.dashboard;

import com.danusys.web.commons.app.EgovMap;

import java.util.Map;

public interface DashboardService {
    EgovMap getDroneData(Map<String, Object> paramMap) throws Exception;
    EgovMap getStatusCnt1(Map<String, Object> paramMap) throws Exception;
    EgovMap getStatusCnt2(Map<String, Object> paramMap) throws Exception;
    EgovMap getStatusCnt3(Map<String, Object> paramMap) throws Exception;
    EgovMap getStatusCnt4(Map<String, Object> paramMap) throws Exception;
    EgovMap getStation(Map<String, Object> paramMap) throws Exception;
    EgovMap getAirPollution(Map<String, Object> paramMap) throws Exception;
}
