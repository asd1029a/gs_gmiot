package com.danusys.web.platform.mapper.common;

import java.util.Map;

public class sqlProviderChain {

    public String selectListQuery(Map<String, Object> paramMap){
        return paramMap.get("qry").toString();
    }
    public String selectOneQuery(Map<String, Object> paramMap){
        return paramMap.get("qry").toString();
    }
    public String selectKeyQuery(Map<String, Object> paramMap){
        return paramMap.get("qry").toString();
    }
    public String insertQuery(Map<String, Object> paramMap){
        return paramMap.get("qry").toString();
    }
    public String updateQuery(Map<String, Object> paramMap){
        return paramMap.get("qry").toString();
    }
    public String deleteQuery(Map<String, Object> paramMap){
        return paramMap.get("qry").toString();
    }
}
