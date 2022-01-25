package com.danusys.web.platform.controller;

import com.danusys.web.platform.model.paging.Page;
import com.danusys.web.platform.model.paging.PagingRequest;
import com.danusys.web.platform.service.notice.NoticeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
    public ResponseEntity<Map<String, Object>> getListNotice(@RequestBody Map<String, Object> paramMap) throws Exception {
        return ResponseEntity.ok().body(noticeService.getList(paramMap));
    }

    @GetMapping(value="/{noticeSeq}")
    public ResponseEntity<Map<String, Object>> getNotice(@PathVariable("noticeSeq") int noticeSeq) throws Exception {
        return ResponseEntity.ok().body(noticeService.getOne(noticeSeq));
    }

    /**
     * 공지사항 : 공지사항 등록
     */
    @PutMapping
    public @ResponseBody String addNotice(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, Object> paramMap) throws Exception {
        return noticeService.insertNotice(paramMap);
    }

    /**
     * 공지사항 : 공지사항 수정
     */
    @PatchMapping
    public @ResponseBody String modNotice(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, Object> paramMap) throws Exception {
        return noticeService.updateNotice(paramMap);
    }

    /**
     * 공지사항 : 공지사항 삭제
     */
    @DeleteMapping
    public @ResponseBody String delNotice(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, Object> paramMap) throws Exception {
        return noticeService.deleteNotice(paramMap);
    }

    @PostMapping(value = "/paging")
    public Page<List<Map<String, Object>>> list(@RequestBody PagingRequest pagingRequest) {
        return noticeService.getLists(pagingRequest);
    }
//    @PostMapping("/getListNotice")
//    public Map<String, Object> notices(@RequestBody Map<String, Object> param,
//                                      @PageableDefault(size=10, sort="id", direction = Sort.Direction.DESC) Pageable pageable) throws  Exception {
//        System.out.println("@@@@@@@@@@@@@@@@@@@@@"+param);
//        Map<String,Object> result = new HashMap<>();
//        Page<Map<String, Object>> notices = noticeService.selectListNotice(param, pageable);
//        result.put("contents", notices);
//        result.put("size", pageable.getPageSize());
//        return result;
//    }

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
