package com.danusys.web.platform.controller;

import com.danusys.web.commons.app.EgovMap;
import com.danusys.web.platform.service.event.EventService;
import com.danusys.web.commons.app.GisUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/event")
@RequiredArgsConstructor
public class EventController {
//    @Value("${}")
//    private String sigCode;

    private final EventService eventService;
    private final com.danusys.web.commons.api.service.EventService jpaEventService;

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

    @GetMapping(value = "/eventKindCodeList/{parentCodeValue}")
    public ResponseEntity<List> getEventKindCodeList(@PathVariable("parentCodeValue") String parentCodeValue) throws Exception {
        List<String> result = jpaEventService.findByParentCodeValue(parentCodeValue);
        return ResponseEntity.ok().body(result);
    }
//
//    @GetMapping(value = "/mntrPageTypeData/{pageTypeCodeValue}")
//    public ResponseEntity<EgovMap> getMntrPageTypeData(@PathVariable("pageTypeCodeValue") String pageTypeCodeValue) throws Exception {
//        Map<String, Object> result = new HashMap<>();
//        List<String> pageTypeList = new ArrayList<>();
//        pageTypeList.stream().forEach(f -> {
//
//        });
//        Map<String, Object> event = new HashMap<>();
//        // List<String> eventStates = jpaEventService.findByParentCodeValue();
//        //        //List<>
//        List<String> eventKinds = jpaEventService.findByParentCodeValue(parentCodeValue);
//        event.put("eventKind", eventKinds);
//        event.put("sigCode", sigCode);
//
//        result.put("event", event);
//
////        return ResponseEntity.ok().body(result);
//        return null;
//    }
}
