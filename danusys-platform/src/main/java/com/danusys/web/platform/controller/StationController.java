package com.danusys.web.platform.controller;

import com.danusys.web.commons.api.model.FacilityOpt;
import com.danusys.web.commons.api.service.FacilityOptService;
import com.danusys.web.commons.app.*;
import com.danusys.web.platform.service.facility.FacilityService;
import com.danusys.web.platform.service.station.StationService;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping(value = "/station")
@RequiredArgsConstructor
public class StationController {

    private final StationService stationService;
    private final FacilityService facilityService;
    private final com.danusys.web.commons.api.service.FacilityService jpaFacilityService;
    private final FacilityOptService facilityOptService;

    /**
     * 개소 : 개소 목록 조회
     */
    @PostMapping
    public ResponseEntity<EgovMap> getList(@RequestBody Map<String, Object> paramMap) throws Exception {
        return ResponseEntity.ok().body(stationService.getList(paramMap));
    }

    /**
     * 개소 : 개소 목록 조회 페이징
     */
    @PostMapping(value = "/paging")
    public ResponseEntity<EgovMap> getListPaging(@RequestBody Map<String, Object> paramMap) throws Exception {
        return ResponseEntity.ok().body(stationService.getListPaging(paramMap));
    }

    /**
     * 개소 : 개소 GEOJSON 목록 조회
     */
    @PostMapping(value = "/geojson", produces = "application/json; charset=utf8")
    public String getListGeoJson(@RequestBody Map<String, Object> paramMap) throws Exception {
        String paramOpt = CommonUtil.validOneNull(paramMap, "option");
        List<Map<String, Object>> heatList = new ArrayList<>(); //인구 분포도 추가리스트
//        AtomicInteger sumCount = new AtomicInteger();

        EgovMap resultEgov = stationService.getList(paramMap);
        List<Map<String, Object>> stationList = (List<Map<String, Object>>) resultEgov.get("data");
        //해당 개소의 시설물들 정보 추가
        stationList.stream().forEach(f -> {
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

                    if(paramOpt.equals("heatMap")) { //인구 분포도 활용한다면
                        String fcltId = CommonUtil.validOneNull(ff, "facilityId");
                        if(fcltId.startsWith("AP")) { //인구 COUNT 시설물
                            facilityOpts.stream().forEach(fo -> {
                                if (fo.getFacilityOptType() == 112) { //인구 OPT
                                    int popCnt = Integer.parseInt(fo.getFacilityOptValue()); //인구 COUNT
                                    f.put("fcltPopCount",popCnt);
//                                    sumCount.getAndAdd(popCnt);
                                    for (int i = 0; i < popCnt; i++) {
                                        heatList.add(f);
                                    }
                                }
                            });
                        }
                    }
                    ff.put("facilityOpts", facilityOpts);
                });

                f.put("facilityList", facilityList);
                //해당개소의 시설물중 영상재생할 cctv가 있는가
//                facilityList.stream().forEach(fclt -> {
//                    //TODO facilityKind로 들어갈 이름 적용하기
//                    if(fclt.getFacilityKind() == "CCTV") {
//                        f.put("cctvVideoFlag", true);
//                    } else {
//                        f.put("cctvVideoFlag", false);
//                    }
//                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        //인구 분포도 추가리스트
        if(paramOpt.equals("heatMap")){
            stationList = Stream.concat(stationList.stream(), heatList.stream()).collect(Collectors.toList());
//            stationList.stream().forEach(f -> {
//                f.put("fcltPopCountSum",sumCount.toString());
//            });
        }
        return GisUtil.getGeoJson(stationList, "station");
    }

    /**
     * 개소 : 개소 단건 조회
     */
    @GetMapping(value = "/{stationSeq}")
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
        ResponseEntity<?> res = null;
        if(paramMap.containsKey("facilitySeqList")){
            res = ResponseEntity.ok().body(stationService.mod(paramMap));
        } else {
            res = ResponseEntity.ok().body(stationService.modOnlyStation(paramMap));
        }
        return res;
    }

    /**
     * 개소 : 개소 삭제
     */
    @DeleteMapping(value = "/{stationSeq}")
    public ResponseEntity<?> del(@PathVariable("stationSeq") int stationSeq) throws Exception {
        stationService.del(stationSeq);
        return ResponseEntity.noContent().build();
    }

    /**
     * 개소 : 개소 목록 엑셀 다운로드
     */
    @ResponseBody
    @PostMapping(value = "/excel/download")
    public void exportNotice(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, Object> paramMap) throws Exception {
        Map<String, Object> dataMap = stationService.getList((Map<String, Object>) paramMap.get("search"));

        paramMap.put("dataMap", dataMap.get("data"));
        Workbook wb = FileUtil.excelDownload(paramMap);
        wb.write(response.getOutputStream());
        wb.close();
    }

    /**
     * 개소 : 사이니지 개소 목록 조회
     */
    @PostMapping(value = "/signage")
    public ResponseEntity<EgovMap> getListStationForSignage(@RequestBody Map<String, Object> paramMap) throws Exception {
        return ResponseEntity.ok().body(stationService.getListStationForSignage(paramMap));
    }
}