package com.danusys.web.platform.service.facilityOpt;

import com.danusys.web.commons.app.EgovMap;

import java.util.Map;

public interface FacilityOptService {
    EgovMap getList(Map<String, Object> paramMap) throws Exception;
    EgovMap getListPaging(Map<String, Object> paramMap) throws Exception;
}
