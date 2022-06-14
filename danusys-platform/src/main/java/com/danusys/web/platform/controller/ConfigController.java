package com.danusys.web.platform.controller;

import com.danusys.web.commons.app.EgovMap;
import com.danusys.web.platform.service.config.ConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.repository.Query;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/config")
@RequiredArgsConstructor
public class ConfigController {
    @Value("${danusys.area.code.sig}")
    private String sigCode;
    private final ConfigService commonService;

    /*
    * 공통코드관리: 공통코드 목록 조회
   */
    @PostMapping(value = "/commonCode")
    public ResponseEntity<EgovMap> getListCommonCode(@RequestBody Map<String, Object> paramMap) throws Exception {
        return ResponseEntity.ok().body(commonService.getListCode(paramMap));
    }

    @GetMapping(value = "/commonCode/{pSeq}")
    public ResponseEntity<EgovMap> getCommonCode(@PathVariable("pSeq") int seq) throws Exception {
        return ResponseEntity.ok().body(commonService.getOneCode(seq));
    }

    @GetMapping(value = "/commonCode/eventKind/{pKind}")
    public ResponseEntity<EgovMap> getCommonCode(@PathVariable("pKind") String pKind) throws Exception {
        return ResponseEntity.ok().body(commonService.getOneEventKind(pKind));
    }

    @GetMapping(value = "/mntrPageTypeData/{pageTypeCodeValue}")
    public ResponseEntity<Map> getListMntrInitParam(@PathVariable("pageTypeCodeValue") String pageTypeCodeValue) throws Exception {

        List<EgovMap> paramList = commonService.getListMntrInitParam(pageTypeCodeValue);
        Map<String, Object> result = new HashMap<>();

        paramList.stream().forEach(f -> {
            result.put(f.get("codeId").toString(),null);
        });

        paramList.stream().forEach(f -> {
            String valueStr = f.get("childValue").toString();
            String keyStr = f.get("codeValue").toString();

            String[] strAry = valueStr.split(",");

            Map<String, Object> each = new HashMap<>();
            each = (Map<String, Object>) result.get(f.get("codeId"));
            if( each == null ){
                each = new HashMap<>();
            }
            each.put(keyStr, strAry);
            each.put("sigCode",sigCode);
            result.put(f.get("codeId").toString(), each);
        });

        return ResponseEntity.ok().body(result);
    }


}
