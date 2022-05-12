package com.danusys.web.platform.controller;

import com.danusys.web.commons.api.dto.ApiParamDto;
import com.danusys.web.platform.service.intellivix.IntellivixService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Project : danusys-webservice-parent
 * Created by Intellij IDEA
 * Developer : ndw85
 * Date : 2022/04/25
 * Time : 16:57
 */

@Slf4j
@RestController
@RequestMapping(value="/intellivix")
public class IntellivixController {
    private final IntellivixService intellivixService;
    public IntellivixController(IntellivixService intellivixService) {
        this.intellivixService = intellivixService;
    }

    @GetMapping(value="/{id}")
    public ResponseEntity<?> getApiKey(@PathVariable("id") Long id) throws Exception {
        return ResponseEntity.ok().body(intellivixService.findById(id));
    }

    @PutMapping(value = "/updateApiKey/{id}")
    public ResponseEntity<?> saveApiParam(@PathVariable("id") Long id, @RequestBody ApiParamDto.Request param) throws Exception {
        intellivixService.updateValue(id,param);
        return ResponseEntity.ok().body("");
    }

}
