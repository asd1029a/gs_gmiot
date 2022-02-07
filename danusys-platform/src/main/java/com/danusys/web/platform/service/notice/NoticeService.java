package com.danusys.web.platform.service.notice;

import com.danusys.web.commons.util.EgovMap;
import java.util.List;

import com.danusys.web.platform.model.paging.Page;
import com.danusys.web.platform.model.paging.PagingRequest;

import java.util.Map;

public interface NoticeService {
    EgovMap getList(Map<String, Object> paramMap) throws Exception;
    EgovMap getOne(int seq) throws Exception;
    int insert(Map<String, Object> paramMap) throws Exception;
    int update(Map<String, Object> paramMap) throws Exception;
    void delete(int seq) throws Exception;
    Page<List<Map<String, Object>>> getLists(PagingRequest pagingRequest);
}
