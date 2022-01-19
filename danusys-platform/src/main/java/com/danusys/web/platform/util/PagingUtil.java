package com.danusys.web.platform.util;

import com.danusys.web.platform.mapper.BoardMapper;
import org.springframework.beans.factory.annotation.Autowired;

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
    public Map<String, Object> createPagingMap(Map<String, Object> paramMap
            , List<HashMap<String, Object>> dataListMap
            , Integer cnt) throws Exception {

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("data", dataListMap);
        resultMap.put("draw", paramMap.get("draw"));

        //Integer cnt = commonDao.selectOneObject(qId.concat("_CNT"), paramMap);
        resultMap.put("recordsTotal", cnt.intValue());
        resultMap.put("recordsFiltered", cnt.intValue());
        return resultMap;
    }
}
