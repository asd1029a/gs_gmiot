package com.danusys.web.platform.controller;

import com.danusys.web.commons.app.EgovMap;
import com.danusys.web.platform.service.dashboard.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping(value = "/dashboard")
public class DashboardController {
    public DashboardController(DashboardService dashboardService) {this.dashboardService = dashboardService;}
    private final DashboardService dashboardService;

    @PostMapping
    public ResponseEntity<EgovMap> getDroneData(@RequestBody Map<String, Object> paramMap) throws Exception {
        return ResponseEntity.ok().body(dashboardService.getDroneData(paramMap));
    }

     /*
    광명       : 승객카운트 - 스마트버스정류장
    영주       : 승객카운트 전체
    부산남구    : 승객카운트 전체
    */
    @PostMapping(value = "/getStatusCnt1")
    public ResponseEntity<EgovMap> getStatusCnt1(@RequestBody Map<String, Object> paramMap) throws Exception {
        return ResponseEntity.ok().body(dashboardService.getStatusCnt1(paramMap));
    }

    /*
    광명       : 승객카운트 - 스마트 폴
    영주       : 스마트버스정류장 통신장애
    부산남구    : 스마트폴 통신장애
    */
    @PostMapping(value = "/getStatusCnt2")
    public ResponseEntity<EgovMap> getStatusCnt2(@RequestBody Map<String, Object> paramMap) throws Exception {
        return ResponseEntity.ok().body(dashboardService.getStatusCnt2(paramMap));
    }

    /*
    광명       : 스마트폴 통신장애
    영주       : 유동인구-쓰러짐 카운트
    부산남구    : 용의자검출 카운트
    */
    @PostMapping(value = "/getStatusCnt3")
    public ResponseEntity<EgovMap> getStatusCnt3(@RequestBody Map<String, Object> paramMap) throws Exception {
        return ResponseEntity.ok().body(dashboardService.getStatusCnt3(paramMap));
    }

    /*
    광명       : 스마트폴 이벤트(전원이벤트)
    영주       : 유동인구-화재 카운트
    부산남구    : 실종자검출 카운트
    */
    @PostMapping(value = "/getStatusCnt4")
    public ResponseEntity<EgovMap> getStatusCnt4(@RequestBody Map<String, Object> paramMap) throws Exception {
        return ResponseEntity.ok().body(dashboardService.getStatusCnt4(paramMap));
    }

    //시간별 유동인구
    @PostMapping(value = "/getFloatingPopulation")
    public ResponseEntity<EgovMap> getFloatingPopulation(@RequestBody Map<String, Object> paramMap) throws Exception {
        return ResponseEntity.ok().body(dashboardService.getFloatingPopulation(paramMap));
    }

    //동별 설치 개소 카운트
    @PostMapping(value = "/getStation")
    public ResponseEntity<EgovMap> getStationCnt(@RequestBody Map<String, Object> paramMap) throws Exception {
        return ResponseEntity.ok().body(dashboardService.getStation(paramMap));
    }

    //환경센서
    @PostMapping(value = "/getAirPollution")
    public ResponseEntity<EgovMap> getAirPollution(@RequestBody Map<String, Object> paramMap) throws Exception {
        return ResponseEntity.ok().body(dashboardService.getAirPollution(paramMap));
    }

    //김제-드론&분전반 상태값
    @PostMapping(value = "/getDroneCabinetStatus")
    public ResponseEntity<EgovMap> getDroneCabinetStatus(@RequestBody Map<String, Object> paramMap) throws Exception {
        return ResponseEntity.ok().body(dashboardService.getDroneCabinetStatus(paramMap));
    }

    //김제-분전반 전력량계
    @PostMapping(value = "/getCabinetRank")
    public ResponseEntity<EgovMap> getCabinetRank(@RequestBody Map<String, Object> paramMap) throws Exception {
        return ResponseEntity.ok().body(dashboardService.getCabinetRank(paramMap));
    }

}
