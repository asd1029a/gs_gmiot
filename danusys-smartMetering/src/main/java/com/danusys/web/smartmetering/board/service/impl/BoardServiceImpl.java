package com.danusys.web.smartmetering.board.service.impl;

import java.util.Map;

import com.danusys.web.smartmetering.board.service.BoardService;
import com.danusys.web.smartmetering.common.dao.CommonDao;
import com.danusys.web.smartmetering.common.util.JsonUtil;
import com.danusys.web.smartmetering.common.util.PagingUtil;
import org.springframework.stereotype.Service;

@Service
public class BoardServiceImpl implements BoardService {


	private final CommonDao commonDao;
	private final PagingUtil pagingUtil;

	public BoardServiceImpl(CommonDao commonDao, PagingUtil pagingUtil) {
		this.commonDao = commonDao;
		this.pagingUtil = pagingUtil;
	}

	/**
	 * 공지사항 목록 조회
	 */
	@Override
	public String selectListBoard(Map<String, Object> paramMap) throws Exception {
		return JsonUtil.getOriJsonString(pagingUtil.getSettingMap("board.SELECT_LIST_BOARD", paramMap));
	}
	
	/**
	 * 공지사항 목록 조회 (메인)
	 */
	@Override
	public String selectListBoardForMain(Map<String, Object> paramMap) throws Exception {
		return JsonUtil.getJsonString(commonDao.selectList("board.SELECT_LIST_BOARD_FOR_MAIN", paramMap));
	}
	
	/**
	 * 공지사항 등록
	 */
	@Override
	public String insertBoard(Map<String, Object> paramMap) throws Exception {
		return JsonUtil.getCntJsonString(commonDao.insert("board.INSERT_BOARD", paramMap));
	}
	
	/**
	 * 공지사항 수정
	 */
	@Override
	public String updateBoard(Map<String, Object> paramMap) throws Exception {
		return JsonUtil.getCntJsonString(commonDao.update("board.UPDATE_BOARD", paramMap));
	}
	
	/**
	 * 공지사항 삭제
	 */
	@Override
	public String deleteBoard(Map<String, Object> paramMap) throws Exception {
		return JsonUtil.getCntJsonString(commonDao.delete("board.DELETE_BOARD", paramMap));
	}
}