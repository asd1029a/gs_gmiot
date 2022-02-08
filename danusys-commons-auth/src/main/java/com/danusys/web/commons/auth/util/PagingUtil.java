package com.danusys.web.commons.auth.util;


import com.danusys.web.commons.util.EgovMap;

import java.util.List;
import java.util.Map;

/*
* 공통 페이징 유틸
* */
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
}
