package com.danusys.web.commons.api.controller;

import com.danusys.web.commons.api.service.AdmService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping(value="/adm")
public class AdmController {
    private AdmService admService;

    public AdmController(AdmService admService) {
        this.admService = admService;
    }

    /**
     * 위도 경도 -> 행정구역 이름 반환 (시도 시군구 읍면동)
     */
    @PostMapping(value = "/lonLatToAdm")
    public ResponseEntity<?> lonLatToAdm(@RequestBody Map<String, Object> paramMap) throws Exception {
        return ResponseEntity.ok().body(admService.findArea(paramMap));
    }
}
