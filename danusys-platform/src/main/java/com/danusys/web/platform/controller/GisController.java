package com.danusys.web.platform.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class GisController {
    @Value("${danusys.area.code.sig}")
    private String siGunCode;

    /**
     * 현 지자체명 반환 (for js)
     * */
    @PostMapping(value = "/getSiGunCode", produces = "application/json; charset=utf8")
    public String getSiGunNm() throws Exception {
        return siGunCode;
    }
}
