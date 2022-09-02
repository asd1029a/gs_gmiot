package com.danusys.web.platform.service.ststistics;

import com.danusys.web.commons.app.EgovMap;

import java.util.List;
import java.util.Map;

public interface StatisticsService {
    EgovMap getList(Map<String, Object> paramMap) throws Exception;

    List<EgovMap> getSumChart(Map<String, Object> paramMap) throws Exception;

    List<EgovMap> getAvgChart(Map<String, Object> paramMap) throws Exception;

    List<EgovMap> getMapChart(Map<String, Object> paramMap) throws Exception;

    EgovMap getGeoJson() throws Exception;

    EgovMap getListOpt(Map<String, Object> paramMap) throws Exception;

    List<EgovMap> getSumChartOpt(Map<String, Object> paramMap) throws Exception;

    List<EgovMap> getAvgChartOpt(Map<String, Object> paramMap) throws Exception;

    List<EgovMap> getMapChartOpt(Map<String, Object> paramMap) throws Exception;

    EgovMap getGeoJsonOpt() throws Exception;
}
