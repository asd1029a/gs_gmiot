package com.danusys.web.platform.util;

import com.danusys.web.commons.util.EgovMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
* 공통 페이징 유틸
* */
public class PagingUtil {

    /*
    * datatable.js 용 페이징 유틸
    * */
    public static EgovMap createPagingMap(Map<String, Object> paramMap, Map<String, Object> dataListMap) throws Exception {
        EgovMap resultMap = new EgovMap();
        resultMap.put("data", dataListMap.get("data"));
        resultMap.put("draw", paramMap.get("draw"));
        resultMap.put("recordsTotal", dataListMap.get("count"));
        resultMap.put("recordsFiltered", dataListMap.get("count"));
        return resultMap;
    }
}
