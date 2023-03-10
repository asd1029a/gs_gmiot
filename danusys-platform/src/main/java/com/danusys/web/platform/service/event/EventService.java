package com.danusys.web.platform.service.event;

import com.danusys.web.commons.app.EgovMap;

import java.util.Map;

public interface EventService {
    EgovMap getList(Map<String, Object> paramMap) throws Exception;
    EgovMap getOne(int seq) throws Exception;
    int mod(Map<String, Object> paramMap) throws Exception;
}
