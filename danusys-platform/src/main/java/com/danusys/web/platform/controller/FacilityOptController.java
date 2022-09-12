package com.danusys.web.platform.controller;

import com.danusys.web.commons.app.*;
import com.danusys.web.platform.service.facilityOpt.FacilityOptService;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@RestController
@RequestMapping(value = "/facilityOpt")
@RequiredArgsConstructor
public class FacilityOptController {

    private final FacilityOptService service;

    @Value("${danusys.area.code.sig}")
    private String sigCode;

    /**
     * 목록 조회
     */
    @PostMapping
    public ResponseEntity<EgovMap> getList(@RequestBody Map<String, Object> paramMap) throws Exception {
        return ResponseEntity.ok().body(service.getList(paramMap));
    }

    /**
     * 목록 조회 페이징
     */
    @PostMapping(value = "/paging")
    public ResponseEntity<EgovMap> getListPaging(@RequestBody Map<String, Object> paramMap) throws Exception {
        return ResponseEntity.ok().body(service.getListPaging(paramMap));
    }
    /**
     * 목록 엑셀 다운로드
     */
    @ResponseBody
    @PostMapping(value = "/excel/download")
    public void exportExcel(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, Object> paramMap) throws Exception {
        Map<String, Object> dataMap = service.getList((Map<String, Object>) paramMap.get("search"));

        paramMap.put("dataMap", dataMap.get("data"));
        Workbook wb = FileUtil.excelDownload(paramMap);
        wb.write(response.getOutputStream());
        wb.close();
    }
}