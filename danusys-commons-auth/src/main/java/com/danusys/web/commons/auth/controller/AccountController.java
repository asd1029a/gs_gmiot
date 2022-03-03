package com.danusys.web.commons.auth.controller;

import com.danusys.web.commons.app.EgovMap;
import com.danusys.web.commons.auth.service.account.AccountService;
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
    public ResponseEntity<EgovMap> getListUser(@RequestBody Map<String, Object> paramMap) throws Exception {
        return ResponseEntity.ok().body(commonService.getListUser(paramMap));
    }

    @GetMapping(value = "/{pSeq}")
    public ResponseEntity<EgovMap> getUser(@PathVariable("pSeq") int seq) throws Exception {
        return ResponseEntity.ok().body(commonService.getOneUser(seq));
    }

    /*
     * 공통코드관리: 공통코드 목록 조회
     */
    @PostMapping(value = "/group")
    public ResponseEntity<EgovMap> getListUserGroupList(@RequestBody Map<String, Object> paramMap) throws Exception {
        return ResponseEntity.ok().body(commonService.getListUserGroup(paramMap));
    }

    @GetMapping(value = "/group/{pSeq}")
    public ResponseEntity<EgovMap> getUserGroup(@PathVariable("pSeq") int seq) throws Exception {
        return ResponseEntity.ok().body(commonService.getOneUserGroup(seq));
    }

}
