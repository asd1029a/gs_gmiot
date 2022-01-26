package com.danusys.web.platform.controller;

import com.danusys.web.commons.util.EgovMap;
import com.danusys.web.platform.service.common.CommonService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@RestController
@RequestMapping(value = "/common")
public class CommonController {
    public CommonController(CommonService commonService) { this.commonService = commonService;}
    private final CommonService commonService;

    /*
    * 공통코드관리: 공통코드 목록 조회
   */
    @PostMapping
    public ResponseEntity<Map<String, Object>> getListCommonCode(@RequestBody Map<String, Object> paramMap) throws Exception {
        return ResponseEntity.ok().body(commonService.selectCodeList(paramMap));
    }

    @GetMapping(value = "/{codeSeq}")
    public ResponseEntity<EgovMap> getCommonCode(@PathVariable("codeSeq") int codeSeq) throws Exception {
        return ResponseEntity.ok().body(commonService.getCodeOne(codeSeq));
    }
}
