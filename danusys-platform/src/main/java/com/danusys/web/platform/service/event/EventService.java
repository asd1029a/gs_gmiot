package com.danusys.web.platform.service.event;

import com.danusys.web.commons.util.EgovMap;

import java.util.Map;

public interface EventService {
    public Map<String, Object> getList(Map<String, Object> paramMap) throws Exception;
    public EgovMap getOne(int seq) throws Exception;
}
