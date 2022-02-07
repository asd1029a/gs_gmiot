package com.danusys.web.platform.service.facility;

import com.danusys.web.commons.util.EgovMap;

import java.util.Map;

public interface FacilityService {
    EgovMap getList(Map<String, Object> paramMap) throws Exception;
    EgovMap getOne(int seq) throws Exception;
    int insert(Map<String, Object> paramMap) throws Exception;
    int insertOpt(Map<String, Object> paramMap) throws Exception;
    int update(Map<String, Object> paramMap) throws Exception;
    int updateOpt(Map<String, Object> paramMap) throws Exception;
    void delete(int seq) throws Exception;
}
