package com.danusys.web.commons.api.controller;

import com.danusys.web.commons.api.model.*;
import com.danusys.web.commons.api.service.ApiExecutorFactoryService;
import com.danusys.web.commons.api.service.ApiExecutorService;
import com.danusys.web.commons.api.service.FacilityService;
import com.danusys.web.commons.api.service.StationService;
import com.danusys.web.commons.api.types.ParamType;
import com.danusys.web.commons.app.CamelUtil;
import com.danusys.web.commons.app.CommonUtil;
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
import java.text.SimpleDateFormat;
import java.util.*;
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

    @PostMapping(value="/getCurrentSkyData")
    public ResponseEntity getCurrentSkyData(@RequestBody Map<String, Object> param) throws Exception {
        //ForecastGridTransfer fcgt = new ForecastGridTransfer( 35.14420140402784, 129.11313119919697, 0);
        //ForecastGridTransfer fcgt = new ForecastGridTransfer(99, 75, 1);
        Map<String, Object> reqParams = (Map<String, Object>) param.get("reqParams");

        //nx, ny 기상청 격자 param 처리
        Double lon = (Double) reqParams.get("lon");
        Double lat = (Double) reqParams.get("lat");
        ForecastGridTransfer fcgt = new ForecastGridTransfer(lat, lon,0);
        Map<String, Object> resultMap = fcgt.transfer();
        reqParams.put("nx",resultMap.get("nx"));
        reqParams.put("ny",resultMap.get("ny"));

        //base_time, base_date param 처리
        Date d = new Date();
        SimpleDateFormat f1 = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat f2 = new SimpleDateFormat("HH");
        String baseDate = f1.format(d);
        String baseTime = Integer.parseInt(f2.format(d))-1+"30";

        if(Integer.parseInt(baseTime) < 1000) baseTime="0"+baseTime;
        reqParams.put("base_date", baseDate);
        reqParams.put("base_time", baseTime);

        param.put("reqPrams", reqParams);

        Api api = getRequestApi(param);
        System.out.println("#######################################");
        System.out.println(api);
        //API DB 정보로 외부 API 호출
        ResponseEntity responseEntity = apiExecutorFactoryService.execute(api);

        String body = (String) responseEntity.getBody();
        ObjectMapper objectMapper = new ObjectMapper();

        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        System.out.println(body);

        Map<String, Object> resultBody = objectMapper.readValue(body, new TypeReference<Map<String, Object>>(){});

        Map<String, Object> dep1 = (Map<String, Object>) resultBody.get("response");
        Map<String, Object> dep2 = (Map<String, Object>) dep1.get("body");
        Map<String, Object> dep3 = (Map<String, Object>) dep2.get("items");

        List<Map<String, Object>> item = (List<Map<String, Object>>) dep3.get("item");

        Map<String, Object > result = new HashMap<>();

        Iterator iterObj = item.iterator();
        while (iterObj.hasNext()) {
            Map<String, Object> result2 = (Map<String, Object>) iterObj.next();
            String category = result2.get("category").toString();
            String fcstDate = result2.get("fcstDate").toString();
            String fcstValue = result2.get("fcstValue").toString();

            //하늘 상태
            if("SKY".equals(category)) {
                if("1".equals(fcstValue)) {
                    result.put("sky", "sunny");
                    result.put("skyNm", "맑음");
                } else if("2".equals(fcstValue) || "4".equals(fcstValue)|| "3".equals(fcstValue) ) {
                    result.put("sky", "cloudy");
                    result.put("skyNm", "구름많음");
                }
            }
            if("PTY".equals(category)) {
                if("1".equals(fcstValue)) {
                    result.put("sky", "rain");
                    result.put("skyNm", "비");
                } else if("2".equals(fcstValue)) {
                    result.put("sky", "sleet");
                    result.put("skyNm", "진눈깨비");
                } else if("3".equals(fcstValue)) {
                    result.put("sky", "snow");
                    result.put("skyNm", "눈");
                } else if("4".equals(fcstValue)) {
                    result.put("sky", "rain");
                    result.put("skyNm", "비");
                }
            }
            //현재 온도
            if("T1H".equals(category)) {
                result.put("tmp", result2.get("fcstValue"));
            }
        };

        System.out.println(result);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @PostMapping(value = "/call")
    public ResponseEntity call(@RequestBody Map<String, Object> param) throws Exception {
        log.trace("param {}", param.toString());
        String callUrl = param.get("callUrl").toString();

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
        ApiParam apiParam = null;
        String callUrl = param.get("callUrl").toString();
        Map<String, Object> reqParams = (Map<String, Object>) param.get("reqParams");

        //API 마스터 정보 가져오기
        api = reqParams == null ? getRequestApi(callUrl) : getRequestApi(callUrl, reqParams);

        return api;
    }

    private Api getRequestApi(String callUrl) {
        Api api = null;
        try {
            log.trace("callUrl : {}", callUrl);

            //API 마스터 정보 가져오기
            api = apiExecutorService.findByCallUrl(StrUtils.getStr(callUrl));

            //요청 컬럼 정보 가져오기
            api.setApiRequestParams(apiExecutorService
                    .findApiParam(api.getId(), ParamType.REQUEST)
                    .stream()
                    .filter(f -> f.getParamType() == ParamType.REQUEST)
                    .collect(toList())
            );

            //응답 컬럼 정보 가져오기
            api.setApiResponseParams(apiExecutorService.findApiParam(api.getId(), ParamType.RESPONSE));

        } catch (Exception ex) {
            log.error(ex.toString());
            throw ex;
        }

        return api;
    }

    private Api getRequestApi(String callUrl, Map<String, Object> reqParams) {
        Api api = null;
        try {
            log.trace("callUrl : {}", callUrl);

            //API 마스터 정보 가져오기
            api = apiExecutorService.findByCallUrl(StrUtils.getStr(callUrl));

            //요청 컬럼 정보 가져오기
            api.setApiRequestParams(apiExecutorService
                    .findApiParam(api.getId(), ParamType.REQUEST)
                    .stream()
                    .filter(f -> f.getParamType() == ParamType.REQUEST)
                    .map((f) -> {
                        final Object p = reqParams.get(f.getFieldNm());
                        if (p != null) f.setValue(p.toString());
                        return f;
                    })
                    .collect(toList())
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

