package com.danusys.web.platform.controller;

import com.danusys.web.commons.app.EgovMap;
import com.danusys.web.platform.service.event.EventService;
import com.danusys.web.commons.app.GisUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
    public ResponseEntity<EgovMap> getListEvent(@RequestBody Map<String, Object> paramMap) throws Exception {
        return ResponseEntity.ok().body(eventService.getList(paramMap));
    }

    /**
     * 관제 > 이벤트 > 조회(geojson)
     */
    @PostMapping(value = "/geojson")
    public String getListEventGeoJson(@RequestBody Map<String, Object> paramMap) throws Exception {
        EgovMap resultEgov = eventService.getList(paramMap);
        List<Map<String,Object>> list = (List<Map<String, Object>>) resultEgov.get("data");
        return GisUtil.getGeoJson(list, "event");
    }

    @GetMapping(value = "/{seq}")
    public ResponseEntity<EgovMap> getEvent(@PathVariable("seq") int seq) throws Exception {
        System.out.println(seq);
        return ResponseEntity.ok().body(eventService.getOne(seq));
    }
}
