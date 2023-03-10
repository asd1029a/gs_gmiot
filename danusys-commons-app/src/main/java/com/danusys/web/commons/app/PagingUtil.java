package com.danusys.web.commons.app;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

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
        resultMap.put("recordsTotal", dataListMap.size());
        resultMap.put("recordsFiltered", dataListMap.size());
        return resultMap;
    }

    public static EgovMap createPagingMap(Map<String, Object> paramMap, Map<String, Object> dataListMap) throws Exception {
        EgovMap resultMap = new EgovMap();
        resultMap.put("data", dataListMap.get("data"));
        resultMap.put("recordsTotal", dataListMap.get("count"));
        resultMap.put("recordsFiltered", dataListMap.get("count"));
        if (dataListMap.get("statusCount") != null)
            resultMap.put("statusCount", dataListMap.get("statusCount"));
        return resultMap;
    }

    public static Pageable getPageableWithSort(int start, int length, List<Sort.Order> orders ) throws Exception {
        int page = start / length;
        return PageRequest.of(page, length, Sort.by(orders));
    }
}
