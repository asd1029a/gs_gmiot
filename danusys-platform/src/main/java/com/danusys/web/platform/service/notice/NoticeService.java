package com.danusys.web.platform.service.notice;

import com.danusys.web.commons.util.EgovMap;
import java.util.List;

import com.danusys.web.platform.model.paging.Page;
import com.danusys.web.platform.model.paging.PagingRequest;

import java.util.Map;

public interface NoticeService {
    Map<String, Object> getList(Map<String, Object> paramMap) throws Exception;
    EgovMap getOne(int noticeSeq) throws Exception;
    String insert(Map<String, Object> paramMap) throws Exception;
    String update(Map<String, Object> paramMap) throws Exception;
    String delete(Map<String, Object> paramMap) throws Exception;
    Page<List<Map<String, Object>>> getLists(PagingRequest pagingRequest);
}
