package com.danusys.web.platform.controller;

import com.danusys.web.commons.util.EgovMap;
import com.danusys.web.platform.service.station.StationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<EgovMap> getListStation(@RequestBody Map<String, Object> paramMap) throws Exception {
        return ResponseEntity.ok().body(stationService.getList(paramMap));
    }

    /**
     * 개소 : 개소 단건 조회
     */
    @GetMapping(value="/{stationSeq}")
    public ResponseEntity<EgovMap> getStation(@PathVariable("stationSeq") int stationSeq) throws Exception {
        return ResponseEntity.ok().body(stationService.getOne(stationSeq));
    }

    /**
     * 개소 : 개소 등록
     */
    @PutMapping
    public ResponseEntity<?> add(@RequestBody Map<String, Object> paramMap) throws Exception {
        return ResponseEntity.ok().body(stationService.insert(paramMap));
    }

    /**
     * 개소 : 개소 수정
     */
    @PatchMapping
    public ResponseEntity<?> mod(@RequestBody Map<String, Object> paramMap) throws Exception {
        return ResponseEntity.ok().body(stationService.update(paramMap));
    }

    /**
     * 개소 : 개소 삭제
     */
    @DeleteMapping(value="/{stationSeq}")
    public ResponseEntity<?> del (@PathVariable("stationSeq") int stationSeq) throws Exception {
        stationService.delete(stationSeq);
        return ResponseEntity.noContent().build();
    }
}
