package com.danusys.web.platform.service.station;

import com.danusys.web.commons.app.EgovMap;

import java.util.List;
import java.util.Map;

public interface StationService {
    EgovMap getList(Map<String, Object> paramMap) throws Exception;
    EgovMap getListPaging(Map<String, Object> paramMap) throws Exception;
    EgovMap getOne(int seq) throws Exception;
    int add(Map<String, Object> paramMap) throws Exception;
    int mod(Map<String, Object> paramMap) throws Exception;
    void del(int seq) throws Exception;
    EgovMap getListStationForSignage(Map<String, Object> paramMap) throws Exception;
    EgovMap getOneStationForSignage(int seq) throws Exception;
}
