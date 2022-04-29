package com.danusys.web.platform.controller;

import com.danusys.web.commons.app.EgovMap;
import com.danusys.web.platform.service.station.StationService;
import com.danusys.web.platform.util.GisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value="/station")
public class StationController {
    public StationController(StationService stationService) { this.stationService = stationService;}

    private final StationService stationService;

    /**
     * 개소 : 개소 목록 조회
     */
    @PostMapping
    public ResponseEntity<EgovMap> getList(@RequestBody Map<String, Object> paramMap) throws Exception {
        return ResponseEntity.ok().body(stationService.getList(paramMap));
    }
    /**
     * 개소 : 개소 목록 조회
     */
    @PostMapping(value = "/paging")
    public ResponseEntity<EgovMap> getListPaging(@RequestBody Map<String, Object> paramMap) throws Exception {
        return ResponseEntity.ok().body(stationService.getListPaging(paramMap));
    }

    /**
     * 개소 : 개소 GEOJSON 목록 조회
     */
    @PostMapping(value="/geojson", produces ="application/json; charset=utf8")
    public String getListGeoJson(@RequestBody Map<String, Object> paramMap) throws Exception {
        EgovMap resultEgov = stationService.getList(paramMap);
        List<Map<String, Object>> list = (List<Map<String, Object>>) resultEgov.get("data");
        return GisUtil.getGeoJson(list,"station");
    }

    /**
     * 개소 : 개소 단건 조회
     */
    @GetMapping(value="/{stationSeq}")
    public ResponseEntity<EgovMap> get(@PathVariable("stationSeq") int stationSeq) throws Exception {
        return ResponseEntity.ok().body(stationService.getOne(stationSeq));
    }

    /**
     * 개소 : 개소 등록
     */
    @PutMapping
    public ResponseEntity<?> add(@RequestBody Map<String, Object> paramMap) throws Exception {
        return ResponseEntity.ok().body(stationService.add(paramMap));
    }

    /**
     * 개소 : 개소 수정
     */
    @PatchMapping
    public ResponseEntity<?> mod(@RequestBody Map<String, Object> paramMap) throws Exception {
        return ResponseEntity.ok().body(stationService.mod(paramMap));
    }

    /**
     * 개소 : 개소 삭제
     */
    @DeleteMapping(value="/{stationSeq}")
    public ResponseEntity<?> del (@PathVariable("stationSeq") int stationSeq) throws Exception {
        stationService.del(stationSeq);
        return ResponseEntity.noContent().build();
    }
}
