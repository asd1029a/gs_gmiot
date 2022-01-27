package com.danusys.web.platform.service.common;

import com.danusys.web.commons.util.EgovMap;

import java.util.List;
import java.util.Map;

public interface CommonService {
    public Map<String, Object> getListCode(Map<String, Object> paramMap) throws Exception;
    public EgovMap getOneCode(int seq) throws Exception;
}
