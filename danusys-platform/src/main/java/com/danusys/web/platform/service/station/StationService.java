package com.danusys.web.platform.service.station;

import com.danusys.web.commons.util.EgovMap;

import java.util.List;
import java.util.Map;

public interface StationService {
    EgovMap getList(Map<String, Object> paramMap) throws Exception;
    EgovMap getOne(int seq) throws Exception;
    int insert(Map<String, Object> paramMap) throws Exception;
    int update(Map<String, Object> paramMap) throws Exception;
    void delete(int seq) throws Exception;
}
