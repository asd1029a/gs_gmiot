package com.danusys.web.commons.auth.controller;

import com.danusys.web.commons.auth.service.account.AccountService;
import com.danusys.web.commons.util.EgovMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping(value = "/user")
public class AccountController {

    public AccountController(AccountService commonService) { this.commonService = commonService;}
    private final AccountService commonService;

    /*
     * 공통코드관리: 공통코드 목록 조회
     */
    @PostMapping(value = "")
    public ResponseEntity<EgovMap> getListCommonCode(@RequestBody Map<String, Object> paramMap) throws Exception {
        return ResponseEntity.ok().body(commonService.getListUser(paramMap));
    }

    @GetMapping(value = "/{pSeq}")
    public ResponseEntity<EgovMap> getCommonCode(@PathVariable("pSeq") int seq) throws Exception {
        return ResponseEntity.ok().body(commonService.getOneUser(seq));
    }

}
