package com.danusys.web.platform.service.notice;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Map;

public interface NoticeService {
    public Map<String, Object> selectListNotice(Map<String, Object> paramMap) throws Exception;
    public String updateNotice(Map<String, Object> paramMap) throws Exception;
    public String deleteNotice(Map<String, Object> paramMap) throws Exception;
    //    public Page<Map<String, Object>> selectListNotice(Map<String, Object> paramMap, Pageable pageable) throws Exception;
    public String insertNotice(Map<String, Object> paramMap) throws Exception;
}
