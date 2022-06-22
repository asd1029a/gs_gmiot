package com.danusys.web.platform.controller;

import com.danusys.web.commons.app.EgovMap;
import com.danusys.web.commons.app.FileUtil;
import com.danusys.web.platform.dto.request.FaceDetectionRequestDto;
import com.danusys.web.platform.service.faceDetection.FaceDetectionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping(value = "/faceDetection")
@RequiredArgsConstructor
public class FaceDetectionController {
    private final FaceDetectionService faceDetectionService;

    /**
     * 얼굴 검출 : 얼굴 목록 조회
     */
    @PostMapping
    public ResponseEntity<EgovMap> getList(@RequestBody Map<String, Object> paramMap) throws Exception {
        return ResponseEntity.ok().body(faceDetectionService.getList(paramMap));
    }

    /**
     * 얼굴 검출 : 얼굴 목록 조회 페이징
     */
    @PostMapping(value = "/paging")
    public ResponseEntity<EgovMap> getListPaging(@RequestBody Map<String, Object> paramMap) throws Exception {
        return ResponseEntity.ok().body(faceDetectionService.getListPaging(paramMap));
    }

    /**
     * 얼굴 검출 : 얼굴 조회
     */
    @GetMapping(value = "/{faceSeq}")
    public ResponseEntity<EgovMap> getOne(@PathVariable("faceSeq") int faceSeq) throws Exception {
        return ResponseEntity.ok().body(faceDetectionService.getOne(faceSeq));
    }

    /**
     * 얼굴 검출 : 얼굴 조회
     */
    @GetMapping(value = "/checkName/{faceName}")
    public ResponseEntity<EgovMap> getOne(@PathVariable("faceName") String faceName) throws Exception {
        return ResponseEntity.ok().body(faceDetectionService.getOne(faceName));
    }

    /**
     * 얼굴 검출 : 얼굴 등록
     */
    @PostMapping(value = "/add", produces = "multipart/form-data")
    public ResponseEntity<?> add(MultipartFile[] file, HttpServletRequest request, FaceDetectionRequestDto faceDetectionRequestDto) throws Exception {
        faceDetectionService.add(file, request, faceDetectionRequestDto);
        return ResponseEntity.noContent().build();
    }

    /**
     * 얼굴 검출 : 얼굴 수정
     */
    @PostMapping(value = "/mod/{faceSeq}", produces = "multipart/form-data")
    public ResponseEntity<?> mod(MultipartFile[] file, HttpServletRequest request, @PathVariable("faceSeq") int faceSeq, FaceDetectionRequestDto faceDetectionRequestDto) throws Exception {
        faceDetectionService.mod(file, request, faceSeq, faceDetectionRequestDto);
        return ResponseEntity.noContent().build();
    }

    /**
     * 얼굴 검출 : 얼굴 삭제
     */
    @DeleteMapping(value = "/{faceSeq}")
    public ResponseEntity<?> del(@PathVariable("faceSeq") int faceSeq) throws Exception {
        faceDetectionService.del(faceSeq);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * 얼굴 검출 : 얼굴 목록 엑셀 다운로드
     */
    @ResponseBody
    @PostMapping(value = "/excel/download")
    public void exportNotice(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, Object> paramMap) throws Exception {
        Map<String, Object> dataMap = faceDetectionService.getList((Map<String, Object>) paramMap.get("search"));

        paramMap.put("dataMap", dataMap.get("data"));
        Workbook wb = FileUtil.excelDownload(paramMap);
        wb.write(response.getOutputStream());
        wb.close();
    }
}