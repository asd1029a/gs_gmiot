package com.danusys.web.commons.api.controller;

import com.danusys.web.commons.api.model.AdmInfo;
import com.danusys.web.commons.api.service.AdmService;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
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

    @PostMapping(value = "/lonLatToAdm")
    public AdmInfo lonLatToAdm(@RequestBody Map<String, Object> paramMap) throws Exception {
        return admService.findArea(paramMap);
    }
}
