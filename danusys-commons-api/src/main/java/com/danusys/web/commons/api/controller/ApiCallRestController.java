package com.danusys.web.commons.api.controller;

import com.danusys.web.commons.api.model.*;
import com.danusys.web.commons.api.service.ApiExecutorFactoryService;
import com.danusys.web.commons.api.service.ApiExecutorService;
import com.danusys.web.commons.api.service.FacilityService;
import com.danusys.web.commons.api.service.StationService;
import com.danusys.web.commons.api.types.ParamType;
import com.danusys.web.commons.app.CamelUtil;
import com.danusys.web.commons.app.StrUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;

/**
 * Project : danusys-webservice-parent
 * Created by IntelliJ IDEA
 * Developer : kai
 * Date : 2021/11/03
 * Time : 2:20 오후
 */
@Slf4j
@RestController
@RequestMapping(value = "/api")
public class ApiCallRestController {
    private ApiExecutorFactoryService apiExecutorFactoryService;
    private ApiExecutorService apiExecutorService;
    private FacilityService facilityService;
    private StationService stationService;

    public ApiCallRestController(ApiExecutorFactoryService apiExecutorFactoryService
            , ApiExecutorService apiExecutorService
            , FacilityService facilityService
            , StationService stationService) {
        this.apiExecutorFactoryService = apiExecutorFactoryService;
        this.apiExecutorService = apiExecutorService;
        this.facilityService = facilityService;
        this.stationService = stationService;
    }

    @PostMapping(value = "/facility")
    public ResponseEntity findAllForFacility(@RequestBody Map<String, Object> param) throws Exception {
        List<Facility> list = facilityService.findAll();

        ObjectMapper objectMapper = new ObjectMapper();
        return ResponseEntity.status(HttpStatus.OK).body(list);
    }

    @PutMapping (value = "/facility")
    public ResponseEntity apiSaveFacility(@RequestBody Map<String, Object> param) throws Exception  {
        log.trace("param {}", param.toString());

        Api api = getRequestApi(param);

        //API DB 정보로 외부 API 호출
        ResponseEntity responseEntity = apiExecutorFactoryService.execute(api);
        String body = (String) responseEntity.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> resultBody = objectMapper.readValue(body, new TypeReference<Map<String, Object>>(){});

        List<Map<String, Object>> list = (List<Map<String, Object>>) resultBody.get("facility_list");

        this.facilityService.saveAll(list);

        return ResponseEntity.status(HttpStatus.OK).body("");
    }

    @PostMapping(value = "/station")
    public ResponseEntity findAllForStation(@RequestBody Map<String, Object> param) throws Exception {
        List<Station> list = stationService.findAll();

        ObjectMapper objectMapper = new ObjectMapper();
        return ResponseEntity.status(HttpStatus.OK).body(list);
    }

    @PutMapping (value = "/station")
    public ResponseEntity apiSaveStation(@RequestBody Map<String, Object> param) throws Exception  {
        log.trace("param {}", param.toString());

        Api api = getRequestApi(param);

        //API DB 정보로 외부 API 호출
        ResponseEntity responseEntity = apiExecutorFactoryService.execute(api);
        String body = (String) responseEntity.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> resultBody = objectMapper.readValue(body, new TypeReference<Map<String, Object>>(){});

        List<Map<String, Object>> list = (List<Map<String, Object>>) resultBody.get("facility_list");

        this.stationService.saveAll(list);
        return ResponseEntity.status(HttpStatus.OK).body("");
    }

    @PostMapping(value="/getWeatherData")
    public ResponseEntity getWeatherData(@RequestBody Map<String, Object> param) throws Exception {
        Api api = getRequestApi(param);

        //ForecastGridTransfer fcgt = new ForecastGridTransfer( 35.14420140402784, 129.11313119919697, 0);
        ForecastGridTransfer fcgt = new ForecastGridTransfer(99, 75, 1);
        System.out.println(fcgt.transfer());

        System.out.println("#######################################");
        System.out.println(api);

        //API DB 정보로 외부 API 호출
        ResponseEntity responseEntity = apiExecutorFactoryService.execute(api);

        String body = (String) responseEntity.getBody();
        ObjectMapper objectMapper = new ObjectMapper();

        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        System.out.println(body);
        Map<String, Object> resultBody = objectMapper.readValue(body, new TypeReference<Map<String, Object>>(){});
//        List<Map<String, Object>> list = (List<Map<String, Object>>) resultBody.get("");
//
//        this.facilityService.saveAll(list);
//
//        return ResponseEntity.status(HttpStatus.OK).body("");
        return null;


        //return this.call(param);
    }

    @PostMapping(value = "/call")
    public ResponseEntity call(@RequestBody Map<String, Object> param) throws Exception {
        log.trace("param {}", param.toString());

        Api api = getRequestApi(param);

        //API DB 정보로 외부 API 호출
        ResponseEntity responseEntity = apiExecutorFactoryService.execute(api);
        String body = (String) responseEntity.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> resultBody = objectMapper.readValue(body, new TypeReference<Map<String, Object>>(){});

        return ResponseEntity.status(HttpStatus.OK)
                .body(resultBody);
    }

    private Api getRequestApi(Map<String, Object> param) {
        Api api = null;
        try {
            log.trace("callUrl : {}", param.get("callUrl"));

            //API 마스터 정보 가져오기
            api = apiExecutorService.findByCallUrl(StrUtils.getStr(param.get("callUrl")));

            //요청 컬럼 정보 가져오기
            api.setApiRequestParams(apiExecutorService
                    .findApiParam(api.getId(), ParamType.REQUEST)
                    .stream()
                    .filter(f -> f.getParamType() == ParamType.REQUEST).collect(toList())
            );

            //응답 컬럼 정보 가져오기
            api.setApiResponseParams(apiExecutorService.findApiParam(api.getId(), ParamType.RESPONSE));

        } catch (Exception ex) {
            log.error(ex.toString());
            throw ex;
        }

        return api;
    }

    /**
     * 파라미터를 API Object에 세팅
     * @param apiParam
     * @param param
     * @return
     */
    private ApiParam apiRequestSetValue(ApiParam apiParam, Map<String, Object> param) {
        log.trace("### 요청 {} => {} = {}", apiParam.getFieldNm(), apiParam.getFieldMapNm(), param.get(CamelUtil.convert2CamelCase(apiParam.getFieldNm())));
        apiParam.setValue(StrUtils.getStr(param.get(CamelUtil.convert2CamelCase(apiParam.getFieldNm()))));
        return apiParam;
    }

    @PostMapping("/deviceInfoList.json")
    public ResponseEntity sample(@RequestBody Map<String, Object> param) throws ParseException {
        final Resource resource = new ClassPathResource("data/deviceInfoList.json");
        String result = "";
        JSONParser jsonParser = new JSONParser();
        try (Reader reader = new InputStreamReader(resource.getInputStream(), "UTF-8")) {
            result = FileCopyUtils.copyToString(reader);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(jsonParser.parse(result));
    }
}

