package com.danusys.web.platform.controller;

import com.danusys.web.commons.app.EgovMap;
import com.danusys.web.commons.app.FileUtil;
import com.danusys.web.platform.dto.request.NoticeRequestDto;
import com.danusys.web.platform.dto.response.NoticeResponseDto;
import com.danusys.web.platform.model.paging.Page;
import com.danusys.web.platform.model.paging.PagingRequest;
import com.danusys.web.platform.service.notice.NoticeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @클래스이름 : NoticeController
 *
 * @작성자 : 강명훈 주임연구원
 * @작성일 : 2022-03-07
 * @설명 : 공지사항 controller
 *
**/

@Slf4j
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
    public ResponseEntity<NoticeResponseDto> getNotice(@PathVariable("noticeSeq") Long noticeSeq) throws Exception {
        return ResponseEntity.ok().body(noticeService.getOne(noticeSeq));
    }

    /**
     * 공지사항 : 공지사항 등록
     */
    @PostMapping(value="/add", produces = "multipart/form-data")
    public ResponseEntity<?> add(MultipartFile[] file, HttpServletRequest request, NoticeRequestDto noticeRequestDto) throws Exception {
        noticeRequestDto.setNoticeFile(FileUtil.uploadAjaxPost(file, request));
        noticeService.add(noticeRequestDto);
        return ResponseEntity.noContent().build();
    }

    /**
     * 공지사항 : 공지사항 수정
     */
    @PostMapping(value="/mod/{noticeSeq}", produces = "multipart/form-data")
    public ResponseEntity<?> mod(MultipartFile[] file, HttpServletRequest request, @PathVariable Long noticeSeq, NoticeRequestDto noticeRequestDto) throws Exception {
        noticeRequestDto.setNoticeFile(FileUtil.uploadAjaxPost(file, request));
        noticeService.mod(noticeSeq, noticeRequestDto);
        return ResponseEntity.noContent().build();
    }

    /**
     * 공지사항 : 공지사항 삭제
     */
    @DeleteMapping(value="/{noticeSeq}")
    public ResponseEntity<?> del (@PathVariable("noticeSeq") Long noticeSeq) throws Exception {
        noticeService.del(noticeSeq);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/download/{fileName:.+}")
    public void download (HttpServletRequest request, HttpServletResponse response,
                          @PathVariable("fileName") String fileName) throws Exception {
        FileUtil.fileDownload(request, response, fileName);
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
