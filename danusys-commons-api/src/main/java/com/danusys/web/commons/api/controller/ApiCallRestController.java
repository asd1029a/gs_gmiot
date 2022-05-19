package com.danusys.web.commons.api.controller;

import com.danusys.web.commons.api.dto.EventReqeustDTO;
import com.danusys.web.commons.api.dto.FacilityDataRequestDTO;
import com.danusys.web.commons.api.model.Api;
import com.danusys.web.commons.api.model.ApiParam;
import com.danusys.web.commons.api.model.Facility;
import com.danusys.web.commons.api.model.Station;
import com.danusys.web.commons.api.service.*;
import com.danusys.web.commons.api.types.BodyType;
import com.danusys.web.commons.api.types.DataType;
import com.danusys.web.commons.api.util.StaticUtil;
import com.danusys.web.commons.app.StrUtils;
import com.danusys.web.commons.mqtt.DanuMqttClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.yaml.snakeyaml.util.UriEncoder;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toMap;

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
@RequiredArgsConstructor
public class ApiCallRestController {
    private final ObjectMapper objectMapper;
    private final ApiExecutorFactoryService apiExecutorFactoryService;
//    private ApiExecutorService apiExecutorService;

    private final ApiCallService apiService;
    private final FacilityService facilityService;
    private final StationService stationService;
    private final ForecastService forecastService;
    private final EventService eventService;
    private final FacilityOptService facilityOptService;
    //private final DanuMqttClient danuMqttClient;
//    private final  CookieService cookieService;
//    private final HttpServletResponse response;


//    public ApiCallRestController(ObjectMapper objectMapper
//            , ApiExecutorFactoryService apiExecutorFactoryService
////            , ApiExecutorService apiExecutorService
//            , ApiService apiService, FacilityService facilityService
//            , StationService stationService
//            , ForecastService forecastService
//            , EventService eventService
//            , FacilityOptService facilityOptService
//            , CookieService cookieService
//            , HttpServletRequest request) {
//        this.objectMapper = objectMapper;
//        this.apiExecutorFactoryService = apiExecutorFactoryService;
//        this.apiService = apiService;
////        this.apiExecutorService = apiExecutorService;
//        this.facilityService = facilityService;
//        this.stationService = stationService;
//        this.forecastService = forecastService;
//        this.eventService = eventService;
//        this.facilityOptService = facilityOptService;
//        this.cookieService = cookieService;
//        this.request = request;
//    }

    @PostMapping(value = "/facility")
    public ResponseEntity findAllForFacility(@RequestBody Map<String, Object> param) throws Exception {
        List<Facility> list = facilityService.findAll();
        return ResponseEntity.status(HttpStatus.OK).body(list);
    }

    @PutMapping (value = "/facility")
    public ResponseEntity apiSaveFacility(@RequestBody Map<String, Object> param) throws Exception  {
        log.trace("param {}", param.toString());

        Api api = apiService.getRequestApi(param);

        //API DB 정보로 외부 API 호출
        ResponseEntity responseEntity = apiExecutorFactoryService.execute(api);
        String body = (String) responseEntity.getBody();
        Map<String, Object> resultBody = objectMapper.readValue(body, new TypeReference<Map<String, Object>>(){});

        List<Map<String, Object>> list = (List<Map<String, Object>>) resultBody.get("facility_list");

        this.facilityService.saveAll(list);

        return ResponseEntity.status(HttpStatus.OK).body("");
    }

    @PostMapping(value = "/station")
    public ResponseEntity findAllForStation(@RequestBody Map<String, Object> param) throws Exception {
        List<Station> list = stationService.findAll();

        return ResponseEntity.status(HttpStatus.OK).body(list);
    }

    @PutMapping (value = "/station")
    public ResponseEntity apiSaveStation(@RequestBody Map<String, Object> param) throws Exception  {
        log.trace("param {}", param.toString());

        Api api = apiService.getRequestApi(param);

        //API DB 정보로 외부 API 호출
        ResponseEntity responseEntity = apiExecutorFactoryService.execute(api);
        String body = (String) responseEntity.getBody();
        Map<String, Object> resultBody = objectMapper.readValue(body, new TypeReference<Map<String, Object>>(){});

        List<Map<String, Object>> list = (List<Map<String, Object>>) resultBody.get("facility_list");

        this.stationService.saveAll(list);
        return ResponseEntity.status(HttpStatus.OK).body("");
    }

    @PostMapping(value="/getCurSkyTmp")
    public ResponseEntity getCurSkyTmp(@RequestBody Map<String, Object> param) throws Exception {
        Api api = apiService.getRequestApi(forecastService.setReqParam(param));
        //API DB 정보로 외부 API 호출
        ResponseEntity responseEntity = apiExecutorFactoryService.execute(api);
        String body = (String) responseEntity.getBody();

        Map<String, Object> resultBody = objectMapper.readValue(body, new TypeReference<Map<String, Object>>(){});
        Map<String, Object> resultMap = forecastService.getCurSkyTmp(resultBody);

        return ResponseEntity.status(HttpStatus.OK).body(resultMap);
    }

    @PostMapping(value = "/getKaKao")
    public ResponseEntity getKaKao(@RequestBody Map<String, Object> param) throws Exception {

        //한글 인코딩 처리
        param.entrySet().stream().peek(f -> {
            f.setValue(UriEncoder.encode(StrUtils.getStr(f.getValue())));
        }).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        Api api = apiService.getRequestApi(param);

        ResponseEntity responseEntity = apiExecutorFactoryService.execute(api);
        String body = (String) responseEntity.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> resultBody = objectMapper.readValue(body, new TypeReference<Map<String, Object>>(){});

        return ResponseEntity.status(HttpStatus.OK).body(resultBody);
    }

    @PostMapping(value = "/call")
    public ResponseEntity call(HttpServletRequest req, @RequestBody Map<String, Object> param) throws Exception {
        log.trace("param {}", param.toString());
        Api api = apiService.getRequestApi(param);

        /**
         * api 요청시 인증 토큰이 필요한 경우
         */
//        apiService.getApiAccessToken(api, request);

        /**
         * API DB 정보로 외부 API 호출
         */
        ResponseEntity responseEntity = apiExecutorFactoryService.execute(api);

        Object resultBody = null;
        if (api.getResponseBodyType() == BodyType.OBJECT_MAPPING) {
            resultBody = responseEntity.getBody();
        } else if (api.getResponseBodyType() == BodyType.ARRAY) {
            String body = (String) responseEntity.getBody();
            resultBody = objectMapper.readValue(body, new TypeReference<List<Map<String, Object>>>() {
            });
        } else if (api.getResponseBodyType() == BodyType.OBJECT) {
            String body = (String) responseEntity.getBody();
            resultBody = objectMapper.readValue(body, new TypeReference<Map<String, Object>>() {
            });
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(resultBody);
    }

    @PostMapping(value = "/ext/send")
    public ResponseEntity extSend(@RequestBody Map<String, Object> param) throws Exception {
        DanuMqttClient danuMqttClient = new DanuMqttClient();
        log.info("param ?? = {}",param);
        Api api = apiService.getRequestApi(param);

        List<ApiParam> apiRequestParams = api.getApiRequestParams();
        List<ApiParam> apiResponseParams = api.getApiResponseParams();
        Map<String, Object> resultBody = new HashMap<>();

        log.trace("external send req data : {}", apiRequestParams.toString());
        log.trace("external send res data : {}", apiResponseParams.toString());

        try {
            // Request check and set
            Map<String, Object> map = apiRequestParams.stream()
                    .filter(f -> f.getDataType().equals(DataType.OBJECT))
                    .peek(f -> {
                        AtomicReference<String> result = new AtomicReference<>();
                        try {
                            result.set(objectMapper.writeValueAsString(param.get(f.getFieldMapNm())));
                        } catch (JsonProcessingException e) {
                            e.printStackTrace();
                        }

                        apiRequestParams.stream().forEach(a -> {
                            result.set(StringUtils.replace(result.get(), a.getFieldMapNm(), a.getFieldNm()));
                        });

                        f.setValue(result.get());
                    })
                    .collect(toMap(ApiParam::getFieldMapNm, ApiParam::getValue));

            // Response check and set
            resultBody = apiResponseParams
                    .stream()
                    .peek(f -> {
                        ApiParam apiParam = apiRequestParams.stream()
                                .filter(ff -> ff.getFieldMapNm().equals(f.getFieldMapNm())).findFirst().get();
                        f.setValue(apiParam.getValue());
                    })
                    .collect(toMap(ApiParam::getFieldMapNm, ApiParam::getValue));

            String checkExist = StaticUtil.checkExist;
            // event transfer
            for(Map.Entry<String, Object> el: map.entrySet()) {
                EventReqeustDTO list = objectMapper.readValue(StrUtils.getStr(el.getValue()), new TypeReference<EventReqeustDTO>() {
                });
                if(list.getEventKind().equals("1") && !checkExist.equals(list.getEventKind()) || (list.getEventKind().equals("1") && checkExist.isEmpty())){
                    //사람있음
                    StaticUtil.checkExist = "1";
                    danuMqttClient.sender("existenceValue","1");
                }else if(list.getEventKind().equals("0") && !checkExist.equals(list.getEventKind()) || (list.getEventKind().equals("0") && checkExist.isEmpty())){
                    //사람없음
                    StaticUtil.checkExist = "0";
                    danuMqttClient.sender("existenceValue","0");
                }
                log.trace(list.toEntity().toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
            resultBody.put("code", "9999");
            return ResponseEntity.status(HttpStatus.OK)
                    .body(resultBody);
        }

        resultBody.put("code", "0000");
        return ResponseEntity.status(HttpStatus.OK)
                .body(resultBody);
    }


    @PostMapping("/event")
    public ResponseEntity apiEvent(@RequestBody Map<String, Object> param) {
        log.info("param  =  {}",param);
        Api api = apiService.getRequestApi(param);

        List<ApiParam> apiRequestParams = api.getApiRequestParams();
        List<ApiParam> apiResponseParams = api.getApiResponseParams();
        Map<String, Object> resultBody = new HashMap<>();

        AtomicReference<DataType> dataType = new AtomicReference<>();

        try {
            // Reqpuest check and set
            Map<String, Object> map = apiRequestParams.stream()
                    .filter(f -> f.getDataType().equals(DataType.ARRAY) || f.getDataType().equals(DataType.OBJECT))
                    .peek(f -> {
                        AtomicReference<String> result = new AtomicReference<>();
                        dataType.set(f.getDataType());
                        try {
                            result.set(objectMapper.writeValueAsString(param.get(f.getFieldMapNm())));
                        } catch (JsonProcessingException e) {
                            e.printStackTrace();
                        }

                        apiRequestParams.stream().forEach(a -> {
                            result.set(StringUtils.replace(result.get(), a.getFieldMapNm(), a.getFieldNm()));
                        });

                        f.setValue(result.get());
                    })
                    .collect(toMap(ApiParam::getFieldMapNm, ApiParam::getValue));

            // Response check and set
            resultBody = apiResponseParams
                    .stream()
                    .peek(f -> {
                        ApiParam apiParam = apiRequestParams.stream()
                                .filter(ff -> ff.getFieldMapNm().equals(f.getFieldMapNm())).findFirst().get();
                        f.setValue(apiParam.getValue());
                    })
                    .collect(toMap(ApiParam::getFieldMapNm, ApiParam::getValue));

            // event save
            for(Map.Entry<String, Object> el: map.entrySet()) {
                if (dataType.get().equals(DataType.ARRAY)) {
                    List<EventReqeustDTO> list = objectMapper.readValue(StrUtils.getStr(el.getValue()), new TypeReference<List<EventReqeustDTO>>() {
                    });
                    eventService.saveAllByEventRequestDTO(list);
                    log.trace(list.toString());
                } else if (dataType.get().equals(DataType.OBJECT)) {
                    EventReqeustDTO eventReqeustDTO = objectMapper.readValue(StrUtils.getStr(el.getValue()), new TypeReference<EventReqeustDTO>() {
                    });
                    eventService.saveByEventRequestDTO(eventReqeustDTO);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            resultBody.put("code", "9999");
            return ResponseEntity.status(HttpStatus.OK)
                    .body(resultBody);
        }

        resultBody.put("code", "0000");
        return ResponseEntity.status(HttpStatus.OK)
                .body(resultBody);
    }

    @PostMapping("facilityData")
    public ResponseEntity facilityData(@RequestBody Map<String, Object> param) {
        Api api = apiService.getRequestApi(param);

        List<ApiParam> apiRequestParams = api.getApiRequestParams();
        List<ApiParam> apiResponseParams = api.getApiResponseParams();
        Map<String, Object> resultBody = new HashMap<>();

        try {
            // Reqpuest check and set
            Map<String, Object> map = apiRequestParams.stream()
                    .filter(f -> f.getDataType().equals(DataType.ARRAY))
                    .peek(f -> {
                        AtomicReference<String> result = new AtomicReference<>();
                        try {
                            result.set(objectMapper.writeValueAsString(param.get(f.getFieldMapNm())));
                        } catch (JsonProcessingException e) {
                            e.printStackTrace();
                        }

                        apiRequestParams.stream().forEach(a -> {
                            result.set(StringUtils.replace(result.get(), a.getFieldMapNm(), a.getFieldNm()));
                        });

                        f.setValue(result.get());
                    })
                    .collect(toMap(ApiParam::getFieldMapNm, ApiParam::getValue));

            // Response check and set
//            resultBody = apiResponseParams
//                    .stream()
//                    .peek(f -> {
//                        ApiParam apiParam = apiRequestParams.stream()
//                                .filter(ff -> ff.getFieldMapNm().equals(f.getFieldMapNm())).findFirst().get();
//                        f.setValue(apiParam.getValue());
//                    })
//                    .collect(toMap(ApiParam::getFieldMapNm, ApiParam::getValue));

            // event save
            for(Map.Entry<String, Object> el: map.entrySet()) {
                List<FacilityDataRequestDTO> list = objectMapper.readValue(StrUtils.getStr(el.getValue()), new TypeReference<List<FacilityDataRequestDTO>>() {
                });

                facilityOptService.saveAllByFacilityDataRequestDTO(list);
                log.trace(list.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
            resultBody.put("code", "9999");
            return ResponseEntity.status(HttpStatus.OK)
                    .body(resultBody);
        }

        resultBody.put("code", "0000");
        return ResponseEntity.status(HttpStatus.OK)
                .body(resultBody);
    }



    private void addEvent() {

    }

//    private Api getRequestApi(String callUrl) {
//        Api api = null;
//        try {
//            log.trace("callUrl : {}", callUrl);
//
//            //API 마스터 정보 가져오기
//            api = apiExecutorService.findByCallUrl(StrUtils.getStr(callUrl));
//
//            //요청 컬럼 정보 가져오기
//            api.setApiRequestParams(apiExecutorService
//                    .findApiParam(api.getId(), ParamType.REQUEST)
//                    .stream()
//                    .filter(f -> f.getParamType() == ParamType.REQUEST)
//                    .collect(toList())
//            );
//
//            //응답 컬럼 정보 가져오기
//            api.setApiResponseParams(apiExecutorService.findApiParam(api.getId(), ParamType.RESPONSE));
//
//        } catch (Exception ex) {
//            log.error(ex.toString());
//            throw ex;
//        }
//
//        return api;
//    }

//    private Api getRequestApi(String callUrl, Map<String, Object> reqParams) {
//        Api api = null;
//        try {
//            log.trace("callUrl : {}", callUrl);
//
//            //API 마스터 정보 가져오기
//            api = apiExecutorService.findByCallUrl(StrUtils.getStr(callUrl));
//
//            //요청 컬럼 정보 가져오기
//            api.setApiRequestParams(apiExecutorService
//                    .findApiParam(api.getId(), ParamType.REQUEST)
//                    .stream()
//                    .filter(f -> f.getParamType() == ParamType.REQUEST)
//                    .map((f) -> {
//                        final Object p = reqParams.get(f.getFieldNm());
//                        if (p != null) f.setValue(p.toString());
//                        return f;
//                    })
//                    .collect(toList())
//            );
//
//            //응답 컬럼 정보 가져오기
//            api.setApiResponseParams(apiExecutorService.findApiParam(api.getId(), ParamType.RESPONSE));
//
//        } catch (Exception ex) {
//            log.error(ex.toString());
//            throw ex;
//        }
//
//        return api;
//    }



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

