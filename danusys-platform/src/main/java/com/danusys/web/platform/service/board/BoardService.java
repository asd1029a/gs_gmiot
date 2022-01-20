package com.danusys.web.platform.service.board;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.HashMap;
import java.util.Map;

public interface BoardService {
    public String selectListBoard(Map<String, Object> paramMap) throws Exception;
    public Page<Map<String, Object>> selectListBoard(Map<String, Object> paramMap, Pageable pageable) throws Exception;
    public String insertBoard(Map<String, Object> paramMap) throws Exception;
    public String updateBoard(Map<String, Object> paramMap) throws Exception;
    public String deleteBoard(Map<String, Object> paramMap) throws Exception;
}
