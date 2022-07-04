package com.danusys.web.platform.controller;

import ch.qos.logback.classic.pattern.SyslogStartConverter;
import com.danusys.web.commons.api.model.FacilityOpt;
import com.danusys.web.commons.api.model.Station;
import com.danusys.web.commons.api.service.FacilityOptService;
import com.danusys.web.commons.app.EgovMap;
import com.danusys.web.commons.app.StrUtils;
import com.danusys.web.platform.service.event.EventService;
import com.danusys.web.commons.app.GisUtil;
import com.danusys.web.platform.service.facility.FacilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/event")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;
    private final StationController stationController;
    private final FacilityService facilityService;
    private final FacilityOptService facilityOptService;
    //private final com.danusys.web.commons.api.service.EventService jpaEventService;

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
        //해당 이벤트의 개소의 시설물들 정보 추가 //TODO station 코드 중복 제거
        list.stream().forEach(f -> {
            Map<String, Object> facilityParam = new LinkedHashMap<>();
            facilityParam.put("stationSeq",f.get("stationSeq").toString());
            facilityParam.put("popType","station");
            try {
                EgovMap facilityMap = facilityService.getList(facilityParam);
                List<Map<String, Object>> facilityList = new ArrayList<>();
                facilityList = (List<Map<String, Object>>) facilityMap.get("data");

                facilityList.stream().forEach(ff -> {
                    String facilitySeq = StrUtils.getStr(ff.get("facilitySeq"));
                    List<FacilityOpt> facilityOpts = facilityOptService.findByFacilitySeqLast(Long.parseLong(facilitySeq));
                    ff.put("facilityOpts", facilityOpts);
                });
                f.put("facilityList", facilityList);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return GisUtil.getGeoJson(list, "event");
    }

    @GetMapping(value = "/{seq}")
    public ResponseEntity<EgovMap> getEvent(@PathVariable("seq") int seq) throws Exception {
        System.out.println(seq);
        return ResponseEntity.ok().body(eventService.getOne(seq));
    }
}
