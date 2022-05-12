package com.danusys.web.commons.api.controller;

import com.danusys.web.commons.api.service.SseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.Map;


/**
 * Project : danusys-webservice-parent
 * Created by Intellij IDEA
 * Developer : ndw85
 * Date : 2022/04/07
 * Time : 19:27
 */
@Slf4j
@RestController
@RequestMapping(value="/sse")
public class SseController {
    private SseService sseService;

    public SseController(SseService sseService) {
        this.sseService = sseService;
    }

    @GetMapping(value = "/{userId}")
    public Flux<ServerSentEvent<String>> connect(@PathVariable("userId") Long userId) {
        return sseService.userCheck(userId);
    }

    @PostMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
    public void send(@RequestBody Map<String,Object> param) {
        sseService.send(param);
    }
}










