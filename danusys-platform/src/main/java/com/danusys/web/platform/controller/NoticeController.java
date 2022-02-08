package com.danusys.web.platform.controller;

import com.danusys.web.commons.util.EgovMap;
import com.danusys.web.platform.model.paging.Page;
import com.danusys.web.platform.model.paging.PagingRequest;
import com.danusys.web.platform.service.notice.NoticeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value="/notice")
public class NoticeController {

    public NoticeController(NoticeService noticeService) { this.noticeService = noticeService; }

    private final NoticeService noticeService;

    /**
     * 공지사항 : 공지사항 목록 조회
     */
    @PostMapping
    public ResponseEntity<EgovMap> getListNotice(@RequestBody Map<String, Object> paramMap) throws Exception {
        return ResponseEntity.ok().body(noticeService.getList(paramMap));
    }

    /**
     * 공지사항 : 공지사항 단건 조회
     */
    @GetMapping(value="/{noticeSeq}")
    public ResponseEntity<EgovMap> getNotice(@PathVariable("noticeSeq") int noticeSeq) throws Exception {
        return ResponseEntity.ok().body(noticeService.getOne(noticeSeq));
    }

    /**
     * 공지사항 : 공지사항 등록
     */
    @PutMapping
    public ResponseEntity<?> add(@RequestBody Map<String, Object> paramMap) throws Exception {
        return ResponseEntity.ok().body(noticeService.add(paramMap));
    }

    /**
     * 공지사항 : 공지사항 수정
     */
    @PatchMapping
    public ResponseEntity<?> mod(@RequestBody Map<String, Object> paramMap) throws Exception {
        return ResponseEntity.ok().body(noticeService.mod(paramMap));
    }

    /**
     * 공지사항 : 공지사항 삭제
     */
    @DeleteMapping(value="/{noticeSeq}")
    public ResponseEntity<?> del (@PathVariable("noticeSeq") int noticeSeq) throws Exception {
        noticeService.del(noticeSeq);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/paging")
    public Page<List<Map<String, Object>>> list(@RequestBody PagingRequest pagingRequest) {
        return noticeService.getLists(pagingRequest);
    }

    /**
     * 공지사항 : 공지사항 엑셀
     */
//    @RequestMapping(value = "/exportExcel.do")
//    public ModelAndView exportNotice(HttpServletRequest request, HttpServletResponse response, @RequestParam Map<String, Object> paramMap) throws Exception {
//        String fileName = "공지사항목록_"+DateUtil.getCurrentDate("yyyyMmddHHmmss");
//        String columnArr = "title|content|insertDt|insertAdminId|updateDt|updateAdminId";
//        String columnNmArr = "제목|내용|입력일|입력 ID|수정일|수정 ID";
//        String qId = "notice.SELECT_LIST_BOARD_EXCEL";
//
//        paramMap.put("columnArr", columnArr);
//        paramMap.put("columnNmArr", columnNmArr);
//        paramMap.put("qId", qId);
//        paramMap.put("fileName", fileName+".xlsx");
//
//        return excelUtil.exportExcel(paramMap);
//    }
}
