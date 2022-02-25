package com.danusys.web.commons.util;

import java.util.List;
import java.util.Map;

public class PagingUtil {
    /*
     * datatable.js 용 페이징 유틸
     * */
    public static EgovMap createPagingMap(Map<String, Object> paramMap, List<?> dataListMap) throws Exception {
        EgovMap resultMap = new EgovMap();
        resultMap.put("data", dataListMap);
        resultMap.put("draw", paramMap.get("draw"));
        resultMap.put("recordsTotal", dataListMap.size());
        resultMap.put("recordsFiltered", dataListMap.size());
        return resultMap;
    }

    public static EgovMap createPagingMap(Map<String, Object> paramMap, Map<String, Object> dataListMap) throws Exception {
        EgovMap resultMap = new EgovMap();
        resultMap.put("data", dataListMap.get("data"));
        resultMap.put("draw", paramMap.get("draw"));
        resultMap.put("recordsTotal", dataListMap.get("count"));
        resultMap.put("recordsFiltered", dataListMap.get("count"));
        return resultMap;
    }
}
