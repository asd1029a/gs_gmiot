package com.danusys.web.platform.service.facility;

import com.danusys.web.commons.app.EgovMap;

import java.util.Map;

public interface FacilityService {
    EgovMap getList(Map<String, Object> paramMap) throws Exception;
    EgovMap getListPaging(Map<String, Object> paramMap) throws Exception;
    EgovMap getOne(int seq) throws Exception;
    int add(Map<String, Object> paramMap) throws Exception;
    int addOpt(Map<String, Object> paramMap) throws Exception;
    int mod(Map<String, Object> paramMap) throws Exception;
    int modOpt(Map<String, Object> paramMap) throws Exception;
    void del(int seq) throws Exception;
    void delOpt(Map<String, Object> paramMap) throws Exception;
    EgovMap getListFacilityInStation(Map<String, Object> paramMap) throws Exception;
    EgovMap getListDimmingGroup(Map<String, Object> paramMap) throws Exception;
    EgovMap getLastDimmingGroupSeq() throws Exception;
    EgovMap getListLampRoadInDimmingGroup(Map<String, Object> paramMap) throws Exception;
    EgovMap getListSignageTemplate(Map<String, Object> paramMap) throws Exception;
}
