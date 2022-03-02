package com.danusys.web.commons.app.controller;

import com.danusys.web.commons.app.model.XssRequestDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Project : danusys-webservice-parent
 * Created by Intellij IDEA
 * Developer : ippo
 * Date : 2022/02/18
 * Time : 09:42
 */
@Slf4j
@RestController
public class XssRequestController {

    @PostMapping(value = "/xssMap")
    public Map<String, Object> xssMap(@RequestBody Map<String, Object> param) {
        log.trace("xssMap >>> {}", param);

        return param;
    }

    @PostMapping(value = "/xss")
    public XssRequestDto xss(@RequestBody XssRequestDto xssRequestDto) {
        log.trace("xss >>> {}", xssRequestDto);

        return xssRequestDto;
    }

    @PostMapping("/form")
    public @ResponseBody
    XssRequestDto form(XssRequestDto xssRequestDto) {
        log.trace("form >>> {}", xssRequestDto);
        return xssRequestDto;
    }
}
