package com.danusys.web.platform.controller;

import com.danusys.web.commons.app.EgovMap;
import com.danusys.web.commons.app.FileUtil;
import com.danusys.web.platform.service.facility.FacilityService;
import com.danusys.web.platform.util.GisUtil;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
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
    public ResponseEntity<EgovMap> getList(@RequestBody Map<String, Object> paramMap) throws Exception {
        return ResponseEntity.ok().body(facilityService.getList(paramMap));
    }
    
    /**
     * 시설물 : 시설물 목록 조회 페이징
     */
    @PostMapping(value="/paging")
    public ResponseEntity<EgovMap> getListPaging(@RequestBody Map<String, Object> paramMap) throws Exception {
        return ResponseEntity.ok().body(facilityService.getListPaging(paramMap));
    }

    /**
     * 시설물 : 시설물 GEOJSON 목록 조회
     */
    @PostMapping(value="/geojson")
    public String getListFacilityGeoJson(@RequestBody Map<String, Object> paramMap) throws Exception {
        EgovMap resultEgov = facilityService.getList(paramMap);
        List<Map<String, Object>> list = (List<Map<String, Object>>) resultEgov.get("data");
        return GisUtil.getGeoJson(list,"facility");
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
    public ResponseEntity<?> add(@RequestBody Map<String, Object> paramMap) throws Exception {
        return ResponseEntity.ok().body(facilityService.add(paramMap));
    }

    /**
     * 시설물 : 시설물 기능 등록
     */
    @PutMapping(value="/opt")
    public ResponseEntity<?> addOpt(@RequestBody Map<String, Object> paramMap) throws Exception {
        return ResponseEntity.ok().body(facilityService.addOpt(paramMap));
    }

    /**
     * 시설물 : 시설물 수정
     */
    @PatchMapping
    public ResponseEntity<?> mod(@RequestBody Map<String, Object> paramMap) throws Exception {
        return ResponseEntity.ok().body(facilityService.mod(paramMap));
    }

    /**
     * 시설물 : 시설물 기능 수정
     */
    @PatchMapping(value="/opt")
    public ResponseEntity<?> modOpt(@RequestBody Map<String, Object> paramMap) throws Exception {
        return ResponseEntity.ok().body(facilityService.modOpt(paramMap));
    }

    /**
     * 시설물 : 시설물 삭제
     */
    @DeleteMapping(value="/{facilitySeq}")
    public ResponseEntity<?> del (@PathVariable("facilitySeq") int facilitySeq) throws Exception {
        facilityService.del(facilitySeq);
        return ResponseEntity.noContent().build();
    }

    /**
     * 시설물 : 시설물 삭제
     */
    @DeleteMapping(value="/opt")
    public ResponseEntity<?> del (@RequestBody Map<String, Object> paramMap) throws Exception {
        facilityService.delOpt(paramMap);
        return ResponseEntity.noContent().build();
    }

    /**
     * 시설물 : 디밍 그룹 조회
     */
    @PostMapping(value="/dimmingGroup")
    public ResponseEntity<EgovMap> getListDimmingGroup(@RequestBody Map<String, Object> paramMap) throws Exception {
        return ResponseEntity.ok().body(facilityService.getListDimmingGroup(paramMap));
    }

    /**
     * 시설물 : 디밍 그룹 조회
     */
    @GetMapping(value="/lastDimmingGroupSeq")
    public ResponseEntity<EgovMap> getLastDimmingGroupSeq() throws Exception {
        return ResponseEntity.ok().body(facilityService.getLastDimmingGroupSeq());
    }

    /**
     * 시설물 : 디밍 그룹 소속 시설물 조회
     */
    @PostMapping(value="/lampRoadInGroup")
    public ResponseEntity<EgovMap> getListLampRoadInDimmingGroup(@RequestBody Map<String, Object> paramMap) throws Exception {
        return ResponseEntity.ok().body(facilityService.getListLampRoadInDimmingGroup(paramMap));
    }

    /**
     * 시설물 : 사이니지 템플릿 목록 조회
     */
    @PostMapping(value="/signageTemplate")
    public ResponseEntity<EgovMap> getListSignageTemplate(@RequestBody Map<String, Object> paramMap) throws Exception {
        return ResponseEntity.ok().body(facilityService.getListSignageTemplate(paramMap));
    }

    /**
     * 시설물 : 시설물 조회 엑셀 다운로드
     */
    @ResponseBody
    @PostMapping(value = "/excel/download")
    public void exportNotice(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, Object> paramMap) throws Exception {
        Map<String, Object> dataMap = facilityService.getList((Map<String, Object>) paramMap.get("search"));

        paramMap.put("dataMap", dataMap.get("data"));
        Workbook wb = FileUtil.excelDownload(paramMap);
        wb.write(response.getOutputStream());
        wb.close();
    }
}
