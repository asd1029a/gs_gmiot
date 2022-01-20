package com.danusys.web.platform.service.board;

import java.util.List;
import java.util.Map;

public interface BoardService {
    public List<Map<String, Object>> selectListBoard(Map<String, Object> paramMap) throws Exception;
    public String insertBoard(Map<String, Object> paramMap) throws Exception;
    public String updateBoard(Map<String, Object> paramMap) throws Exception;
    public String deleteBoard(Map<String, Object> paramMap) throws Exception;
}
