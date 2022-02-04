package com.danusys.web.platform.controller;

import com.danusys.web.commons.util.EgovMap;
import com.danusys.web.platform.service.facility.FacilityService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping(value="/facility")
public class FacilityController {

    public FacilityController(FacilityService facilityService) { this.facilityService = facilityService; }

    private final FacilityService facilityService;

    /**
     * 시설물 : 시설물 목록 조회
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> getListFacility(@RequestBody Map<String, Object> paramMap) throws Exception {
        return ResponseEntity.ok().body(facilityService.getList(paramMap));
    }

    /**
     * 시설물 : 시설물 단건 조회
     */
    @GetMapping(value="/{facilitySeq}")
    public ResponseEntity<EgovMap> getFacility(@PathVariable("facilitySeq") int facilitySeq) throws Exception {
        return ResponseEntity.ok().body(facilityService.getOne(facilitySeq));
    }

    /**
     * 시설물 : 시설물 등록
     */
    @PutMapping
    public int add(@RequestBody Map<String, Object> paramMap) throws Exception {
        return facilityService.insert(paramMap);
    }

    /**
     * 시설물 : 시설물 기능 등록
     */
    @PutMapping(value="/opt")
    public int addOpt(@RequestBody Map<String, Object> paramMap) throws Exception {
        return facilityService.insertOpt(paramMap);
    }

    /**
     * 시설물 : 시설물 수정
     */
    @PatchMapping
    public int mod(@RequestBody Map<String, Object> paramMap) throws Exception {
        return facilityService.update(paramMap);
    }

    /**
     * 시설물 : 시설물 기능 수정
     */
    @PatchMapping(value="/opt")
    public int modOpt(@RequestBody Map<String, Object> paramMap) throws Exception {
        return facilityService.updateOpt(paramMap);
    }

    /**
     * 시설물 : 시설물 삭제
     */
    @DeleteMapping(value="/{facilitySeq}")
    public void del (@PathVariable("facilitySeq") int facilitySeq) throws Exception {
        facilityService.delete(facilitySeq);
    }
}
