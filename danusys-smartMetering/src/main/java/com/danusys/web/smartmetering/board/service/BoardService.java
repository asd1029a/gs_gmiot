package com.danusys.web.smartmetering.board.service;

import java.util.Map;

public interface BoardService {
	public String selectListBoard(Map<String, Object> paramMap) throws Exception;
	public String selectListBoardForMain(Map<String, Object> paramMap) throws Exception;
	public String insertBoard(Map<String, Object> paramMap) throws Exception;
	public String updateBoard(Map<String, Object> paramMap) throws Exception;
	public String deleteBoard(Map<String, Object> paramMap) throws Exception;
}