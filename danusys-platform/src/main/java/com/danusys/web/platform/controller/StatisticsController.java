package com.danusys.web.platform.controller;

import com.danusys.web.commons.app.EgovMap;
import com.danusys.web.platform.service.ststistics.StatisticsService;
import com.danusys.web.commons.app.GisUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/stats")
public class StatisticsController {
    public StatisticsController(StatisticsService statisticsService) { this.statisticsService = statisticsService;}
    private final StatisticsService statisticsService;

    @PostMapping(value = "/list")
    public ResponseEntity<EgovMap> getList(@RequestBody Map<String, Object> paramMap) throws Exception {
        return ResponseEntity.ok().body(statisticsService.getList(paramMap));
    }
    @PostMapping(value = "/sumChart")
    public ResponseEntity<List<EgovMap>> getSumChart(@RequestBody Map<String, Object> paramMap) throws Exception {
        return ResponseEntity.ok().body(statisticsService.getSumChart(paramMap));
    }
    @PostMapping(value = "/avgChart")
    public ResponseEntity<List<EgovMap>> getAvgChart(@RequestBody Map<String, Object> paramMap) throws Exception {
        return ResponseEntity.ok().body(statisticsService.getAvgChart(paramMap));
    }
    @PostMapping(value = "/mapChart")
    public ResponseEntity<List<EgovMap>> getMapChart(@RequestBody Map<String, Object> paramMap) throws Exception {
        return ResponseEntity.ok().body(statisticsService.getMapChart(paramMap));
    }
    @PostMapping(value = "/geoJson")
    public String getGeoJson(HttpServletRequest httpServletRequest) throws Exception {
        EgovMap resultEgov = statisticsService.getGeoJson();
        List<Map<String,Object>> list = (List<Map<String, Object>>) resultEgov.get("data");
        return GisUtil.getGeoJsonPolygon(list, "emd");
    }

    @PostMapping(value = "/listOpt")
    public ResponseEntity<EgovMap> getListOpt(@RequestBody Map<String, Object> paramMap) throws Exception {
        return ResponseEntity.ok().body(statisticsService.getListOpt(paramMap));
    }
    @PostMapping(value = "/sumChartOpt")
    public ResponseEntity<List<EgovMap>> getSumChartOpt(@RequestBody Map<String, Object> paramMap) throws Exception {
        return ResponseEntity.ok().body(statisticsService.getSumChartOpt(paramMap));
    }
    @PostMapping(value = "/avgChartOpt")
    public ResponseEntity<List<EgovMap>> getAvgChartOpt(@RequestBody Map<String, Object> paramMap) throws Exception {
        return ResponseEntity.ok().body(statisticsService.getAvgChartOpt(paramMap));
    }
    @PostMapping(value = "/mapChartOpt")
    public ResponseEntity<List<EgovMap>> getMapChartOpt(@RequestBody Map<String, Object> paramMap) throws Exception {
        return ResponseEntity.ok().body(statisticsService.getMapChartOpt(paramMap));
    }
    @PostMapping(value = "/geoJsonOpt")
    public String getGeoJsonOpt(HttpServletRequest httpServletRequest) throws Exception {
        EgovMap resultEgov = statisticsService.getGeoJsonOpt();
        List<Map<String,Object>> list = (List<Map<String, Object>>) resultEgov.get("data");
        return GisUtil.getGeoJsonPolygon(list, "emd");
    }
}
