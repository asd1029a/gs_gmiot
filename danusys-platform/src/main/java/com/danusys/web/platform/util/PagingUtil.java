package com.danusys.web.platform.util;

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
    public static Map<String, Object> createPagingMap(Map<String, Object> paramMap, List<Map<String, Object>> dataListMap) throws Exception {

        Map<String, Object> resultMap = new HashMap<String, Object>();
        resultMap.put("data", dataListMap);
        resultMap.put("draw", paramMap.get("draw"));
        resultMap.put("recordsTotal", dataListMap.size());
        resultMap.put("recordsFiltered", dataListMap.size());
        return resultMap;
    }
}
