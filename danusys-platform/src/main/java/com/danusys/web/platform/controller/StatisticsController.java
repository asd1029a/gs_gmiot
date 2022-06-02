package com.danusys.web.platform.controller;

import com.danusys.web.commons.app.EgovMap;
import com.danusys.web.platform.service.ststistics.StatisticsService;
import com.danusys.web.commons.app.GisUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/stats")
public class StatisticsController {
    public StatisticsController(StatisticsService statisticsService) { this.statisticsService = statisticsService;}
    private final StatisticsService statisticsService;

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
}
