package com.danusys.web.platform.service.notice;

import java.util.List;

import com.danusys.web.platform.model.paging.Page;
import com.danusys.web.platform.model.paging.PagingRequest;

import java.util.Map;

public interface NoticeService {
    public Map<String, Object> getList(Map<String, Object> paramMap) throws Exception;
    public Map<String, Object> getOne(int noticeSeq) throws Exception;
    public String updateNotice(Map<String, Object> paramMap) throws Exception;
    public String deleteNotice(Map<String, Object> paramMap) throws Exception;
//    public Page<Map<String, Object>> selectListNotice(Map<String, Object> paramMap, Pageable pageable) throws Exception;
    public String insertNotice(Map<String, Object> paramMap) throws Exception;
    public Page<List<Map<String, Object>>> getLists(PagingRequest pagingRequest);
}
