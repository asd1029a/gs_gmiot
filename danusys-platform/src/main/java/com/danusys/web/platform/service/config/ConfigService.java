package com.danusys.web.platform.service.config;

import com.danusys.web.commons.app.EgovMap;

import java.util.Map;

public interface ConfigService {
    EgovMap getListCode(Map<String, Object> paramMap) throws Exception;
    EgovMap getOneCode(int seq) throws Exception;
    EgovMap getOneEventKind(String pKind);
}
