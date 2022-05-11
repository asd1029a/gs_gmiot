package com.danusys.web.platform.controller;

import com.danusys.web.commons.app.EgovMap;
import com.danusys.web.platform.service.faceDetection.FaceDetectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping(value = "/faceDetection")
@RequiredArgsConstructor
public class faceDetectionController {
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
}
