package com.danusys.web.platform.controller;

import com.danusys.web.commons.util.EgovMap;
import com.danusys.web.platform.service.event.EventService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping(value = "/event")
public class EventController {
    public EventController(EventService eventService) { this.eventService = eventService;}
    private final EventService eventService;

    /*
    * 조회/관리 > 이벤트관리 > 스마트 도시 이벤트 목록 조희
    * */
    @PostMapping
    public ResponseEntity<Map<String, Object>> getListEvent(@RequestBody Map<String, Object> paramMap) throws Exception {
        return ResponseEntity.ok().body(eventService.getList(paramMap));
    }
    @GetMapping(value = "/{seq}")
    public ResponseEntity<EgovMap> getEvent(@PathVariable("seq") int seq) throws Exception {
        System.out.println(seq);
        return ResponseEntity.ok().body(eventService.getOne(seq));
    }
}
