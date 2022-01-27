package com.danusys.web.platform.service.config;

import com.danusys.web.commons.util.EgovMap;

import java.util.Map;

public interface ConfigService {
    public Map<String, Object> getListCode(Map<String, Object> paramMap) throws Exception;
    public EgovMap getOneCode(int seq) throws Exception;
}
