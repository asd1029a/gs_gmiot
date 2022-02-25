package com.danusys.web.platform.service.notice;

import com.danusys.web.commons.app.EgovMap;
import java.util.List;

import com.danusys.web.platform.model.paging.Page;
import com.danusys.web.platform.model.paging.PagingRequest;

import java.util.Map;

public interface NoticeService {
    EgovMap getList(Map<String, Object> paramMap) throws Exception;
    EgovMap getOne(int seq) throws Exception;
    int add(Map<String, Object> paramMap) throws Exception;
    int mod(Map<String, Object> paramMap) throws Exception;
    void del(int seq) throws Exception;

    Page<List<Map<String, Object>>> getLists(PagingRequest pagingRequest);
}
