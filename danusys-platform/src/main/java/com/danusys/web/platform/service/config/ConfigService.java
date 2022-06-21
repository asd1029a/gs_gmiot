package com.danusys.web.platform.service.config;

import com.danusys.web.commons.app.EgovMap;

import java.util.List;
import java.util.Map;

public interface ConfigService {
    EgovMap getListCode(Map<String, Object> paramMap) throws Exception;
    EgovMap getOneCode(int seq) throws Exception;
    EgovMap getOneEventKind(String pKind);
    List<EgovMap> getListMntrInitParam(String pageTypeCodeValue) throws Exception;
    EgovMap getVideoNetInfo(String ipClassAB) throws Exception;
    List<EgovMap> getVideoConfig() throws Exception;
}
