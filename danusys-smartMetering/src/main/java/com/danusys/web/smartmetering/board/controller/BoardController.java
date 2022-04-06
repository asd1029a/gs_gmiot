package com.danusys.web.smartmetering.board.controller;

import com.danusys.web.smartmetering.board.service.BoardService;
import com.danusys.web.smartmetering.common.annotation.JsonRequestMapping;
import com.danusys.web.smartmetering.common.util.DateUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Controller
public class BoardController {

	private final BoardService boardService;

	public BoardController(BoardService boardService) {	this.boardService = boardService;}

	// @Autowired
	//ExcelUtil excelUtil;
	
	/**
	 * ################
	 * 공지사항
	 * ################
	 */
	 
	@RequestMapping(value = {"/board/boardList.do", "/{sub}/board/boardList.do"})
	public String boardList(HttpServletRequest request, HttpServletResponse response, @PathVariable(name = "sub", required = false) String sub) throws Exception {
		return "board/boardList";
	}
	
	/**
	 * 공지사항 : 공지사항 목록 조회
	 */
	@JsonRequestMapping(value = "/board/getListBoard.ado")
	public @ResponseBody String getListBoard(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, Object> paramMap) throws Exception {
		return boardService.selectListBoard(paramMap);
	}
	
	/**
	 * 공지사항 : 공지사항 목록 조회 (메인)

	@JsonRequestMapping(value = "/board/getListBoardForMain.ado")
	public @ResponseBody String getListBoardForMain(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, Object> paramMap) throws Exception {
		return boardService.selectListBoardForMain(paramMap);
	}
	 */
	@PostMapping(value = "/board/getListBoardForMain.ado")
	public @ResponseBody String getListBoardForMain(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, Object> paramMap) throws Exception {
		return boardService.selectListBoardForMain(paramMap);
	}


	/**
	 * 공지사항 : 공지사항 등록
	 */
	@JsonRequestMapping(value = "/board/addBoard.ado", method = RequestMethod.PUT )
	public @ResponseBody String addBoard(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, Object> paramMap) throws Exception {
		return boardService.insertBoard(paramMap);
	}
	
	/**
	 * 공지사항 : 공지사항 수정
	 */
	@JsonRequestMapping(value = "/board/modBoard.ado", method = RequestMethod.PATCH)
	public @ResponseBody String modBoard(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, Object> paramMap) throws Exception {
		return boardService.updateBoard(paramMap);
	}
	
	/**
	 * 공지사항 : 공지사항 삭제
	 */
	@JsonRequestMapping(value = "/board/delBoard.ado", method = RequestMethod.DELETE)
	public @ResponseBody String delBoard(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, Object> paramMap) throws Exception {
		return boardService.deleteBoard(paramMap);
	}
	
	/**
	 * 공지사항 : 공지사항 엑셀
	 */
	@JsonRequestMapping(value = "/board/exportExcelBoard.do")
	public ModelAndView exportBoard(HttpServletRequest request, HttpServletResponse response, @RequestParam Map<String, Object> paramMap) throws Exception {
		String fileName = "공지사항목록_"+DateUtil.getCurrentDate("yyyyMmddHHmmss");
		String columnArr = "title|content|insertDt|insertAdminId|updateDt|updateAdminId";
		String columnNmArr = "제목|내용|입력일|입력 ID|수정일|수정 ID";
		String qId = "board.SELECT_LIST_BOARD_EXCEL";
		
		paramMap.put("columnArr", columnArr);
		paramMap.put("columnNmArr", columnNmArr);
		paramMap.put("qId", qId);
		paramMap.put("fileName", fileName+".xlsx");
		
		//return excelUtil.exportExcel(paramMap);
		return null;
	}

}


