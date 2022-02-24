package com.danusys.web.platform.controller;

import com.danusys.web.commons.app.EgovMap;
import com.danusys.web.platform.service.config.ConfigService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping(value = "/config")
public class ConfigController {
    public ConfigController(ConfigService commonService) { this.commonService = commonService;}
    private final ConfigService commonService;

    /*
    * 공통코드관리: 공통코드 목록 조회
   */
    @PostMapping(value = "/commonCode")
    public ResponseEntity<EgovMap> getListCommonCode(@RequestBody Map<String, Object> paramMap) throws Exception {
        return ResponseEntity.ok().body(commonService.getListCode(paramMap));
    }

    @GetMapping(value = "/commonCode/{pSeq}")
    public ResponseEntity<EgovMap> getCommonCode(@PathVariable("pSeq") int seq) throws Exception {
        return ResponseEntity.ok().body(commonService.getOneCode(seq));
    }
}
