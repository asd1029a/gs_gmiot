package com.danusys.web.platform.controller;

import com.danusys.web.platform.service.board.BoardService;
import com.danusys.web.platform.util.PagingUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/board")
public class BoardController {

    public BoardController(BoardService boardService) { this.boardService = boardService }

    private final BoardService boardService;

    /**
     * 공지사항 : 공지사항 목록 조회
     */
    @PostMapping(value = "/board/getListBoard")
    public ResponseEntity<List<Map<String, Object>>> getListBoard(
            HttpServletRequest request
            , HttpServletResponse response
            , @RequestBody Map<String, Object> paramMap) throws Exception {

        return ResponseEntity.ok().body(boardService.selectListBoard(paramMap));
    }

    /**
     * 공지사항 : 데이터 테이블 조회
     */
    @PostMapping(value = "/board/createTableBoard")
    public ResponseEntity<Map<String, Object>> createTableBoard(
            HttpServletRequest request
            , HttpServletResponse response
            , @RequestBody Map<String, Object> paramMap) throws Exception {

        Map<String, Object> resultMap = PagingUtil.createPagingMap(paramMap, boardService.selectListBoard(paramMap));

        return ResponseEntity.ok().body(resultMap);
    }

    /**
     * 공지사항 : 공지사항 등록
     */
    @PutMapping(value = "/board/addBoard")
    public @ResponseBody String addBoard(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, Object> paramMap) throws Exception {
        return boardService.insertBoard(paramMap);
    }

    /**
     * 공지사항 : 공지사항 수정
     */
    @PatchMapping(value = "/board/modBoard")
    public @ResponseBody String modBoard(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, Object> paramMap) throws Exception {
        return boardService.updateBoard(paramMap);
    }

    /**
     * 공지사항 : 공지사항 삭제
     */
    @DeleteMapping(value = "/board/delBoard.ado")
    public @ResponseBody String delBoard(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, Object> paramMap) throws Exception {
        return boardService.deleteBoard(paramMap);
    }

    /**
     * 공지사항 : 공지사항 엑셀
     */
//    @RequestMapping(value = "/board/exportExcel.do")
//    public ModelAndView exportBoard(HttpServletRequest request, HttpServletResponse response, @RequestParam Map<String, Object> paramMap) throws Exception {
//        String fileName = "공지사항목록_"+DateUtil.getCurrentDate("yyyyMmddHHmmss");
//        String columnArr = "title|content|insertDt|insertAdminId|updateDt|updateAdminId";
//        String columnNmArr = "제목|내용|입력일|입력 ID|수정일|수정 ID";
//        String qId = "board.SELECT_LIST_BOARD_EXCEL";
//
//        paramMap.put("columnArr", columnArr);
//        paramMap.put("columnNmArr", columnNmArr);
//        paramMap.put("qId", qId);
//        paramMap.put("fileName", fileName+".xlsx");
//
//        return excelUtil.exportExcel(paramMap);
//    }

}
