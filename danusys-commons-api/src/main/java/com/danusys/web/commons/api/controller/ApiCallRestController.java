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
import com.danusys.web.commons.api.types.ParamType;
import com.danusys.web.commons.app.CamelUtil;
import com.danusys.web.commons.app.StrUtils;
import com.danusys.web.commons.app.service.CookieService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import javax.servlet.http.Cookie;
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

import static java.util.stream.Collectors.toList;
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
public class ApiCallRestController {
    private ObjectMapper objectMapper;
    private ApiExecutorFactoryService apiExecutorFactoryService;
    private ApiExecutorService apiExecutorService;
    private FacilityService facilityService;
    private StationService stationService;
    private ForecastService forecastService;
    private EventService eventService;
    private FacilityOptService facilityOptService;
    private CookieService cookieService;

    public ApiCallRestController(ObjectMapper objectMapper
            , ApiExecutorFactoryService apiExecutorFactoryService
            , ApiExecutorService apiExecutorService
            , FacilityService facilityService
            , StationService stationService
            , ForecastService forecastService
            , EventService eventService
            , FacilityOptService facilityOptService
            , CookieService cookieService) {
        this.objectMapper = objectMapper;
        this.apiExecutorFactoryService = apiExecutorFactoryService;
        this.apiExecutorService = apiExecutorService;
        this.facilityService = facilityService;
        this.stationService = stationService;
        this.forecastService = forecastService;
        this.eventService = eventService;
        this.facilityOptService = facilityOptService;
        this.cookieService = cookieService;
    }

    @PostMapping(value = "/facility")
    public ResponseEntity findAllForFacility(@RequestBody Map<String, Object> param) throws Exception {
        List<Facility> list = facilityService.findAll();
        return ResponseEntity.status(HttpStatus.OK).body(list);
    }

    @PutMapping (value = "/facility")
    public ResponseEntity apiSaveFacility(@RequestBody Map<String, Object> param) throws Exception  {
        log.trace("param {}", param.toString());

        Api api = getRequestApi(param);

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

        Api api = getRequestApi(param);

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
        Api api = getRequestApi(forecastService.setReqParam(param));
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
        Api api = getRequestApi(param);

        ResponseEntity responseEntity = apiExecutorFactoryService.execute(api);
        String body = (String) responseEntity.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> resultBody = objectMapper.readValue(body, new TypeReference<Map<String, Object>>(){});

        return ResponseEntity.status(HttpStatus.OK).body(resultBody);
    }


//    @PostMapping(value = "/call2")
//    public ResponseEntity call2(@RequestBody Map<String, Object> param) throws Exception {
//        log.trace("param {}", param.toString());
//
//        Api api = getRequestApi(param);
//
//        //API DB 정보로 외부 API 호출
//        ResponseEntity responseEntity = apiExecutorFactoryService.execute(api);
//
//
//        String authInfo = StrUtils.getStr(api.getAuthInfo());
//        if (authInfo.contains("bearer")) {
//            String accessToken = Arrays.asList(servletRequest.getCookies()).stream().filter(f -> f.getName().equals("kuto_access_token")).collect()
//            if(accessToken) {
//
//                Map<String, Object> param2 = new HashMap<>();
//                param2.put("callUrl", "/kudo/login");
//                param2.put()
//                Api api = getRequestApi(param);
//
//                //API DB 정보로 외부 API 호출
//                ResponseEntity responseEntity = apiExecutorFactoryService.execute(api);
//                Map<> param2 = a
//                Api api2 = getRequestApi(param);
//            }
//
//
//
//        }
//
//        String body = (String) responseEntity.getBody();
//        ObjectMapper objectMapper = new ObjectMapper();
//        Map<String, Object> resultBody = objectMapper.readValue(body, new TypeReference<Map<String, Object>>(){});
//
//        return ResponseEntity.status(HttpStatus.OK)
//                .body(resultBody);
//    }

//    @PostMapping(value = "/call3")
//    public ResponseEntity call3(@RequestBody Map<String, Object> param) throws Exception {
//        log.trace("param {}", param.toString());
//
//
//        Api api = getRequestApi(param);
//
//        //API DB 정보로 외부 API 호출
//        ResponseEntity responseEntity = apiExecutorFactoryService.execute(api);
//        ObjectMapper objectMapper = new ObjectMapper();
//        String body = (String) responseEntity.getBody();
//        Object resultBody = null;
//        // TODO : List 와 Map 형태를 구분 임시 처리
//        if (body.indexOf("[") == 0) {
//
//            List<Map<String, Object>> result = objectMapper.readValue(body, new TypeReference<List<Map<String, Object>>>(){});
//            resultBody = result;
//        } else if (body.indexOf("{") == 0) {
//            Map<String, Object> result = objectMapper.readValue(body, new TypeReference<Map<String, Object>>(){});
//            resultBody = result;
//        }
//
//        return ResponseEntity.status(HttpStatus.OK)
//                .body(resultBody);
//    }


    @PostMapping(value = "/call")
    public ResponseEntity call(HttpServletRequest req, @RequestBody Map<String, Object> param) throws Exception {
        log.trace("param {}", param.toString());

        Api api = getRequestApi(param);

        /**
         * api 요청시 인증 토큰이 필요한 경우
         */
        String accessToken = this.getApiAccessToken(api, req);

        /**
         * API DB 정보로 외부 API 호출
         */
        ResponseEntity responseEntity = apiExecutorFactoryService.execute(api);

        Map<String, Object> resultBody = null;
        if( api.getResponseBodyType() == BodyType.OBJECT_MAPPING) {
            resultBody = (Map<String, Object>) responseEntity.getBody();
        } else {
            String body = (String) responseEntity.getBody();
            resultBody = objectMapper.readValue(body, new TypeReference<Map<String, Object>>() {
            });
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(resultBody);
    }

    @PostMapping(value = "/ext/send")
    public ResponseEntity extSend(@RequestBody Map<String, Object> param) throws Exception {
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


    @PostMapping("event")
    public ResponseEntity apiEvent(@RequestBody Map<String, Object> param) {
        Api api = getRequestApi(param);

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
                List<EventReqeustDTO> list = objectMapper.readValue(StrUtils.getStr(el.getValue()), new TypeReference<List<EventReqeustDTO>>() {
                });
                eventService.saveAllByEeventRequestDTO(list);
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

    @PostMapping("facilityData")
    public ResponseEntity facilityData(@RequestBody Map<String, Object> param) {
        Api api = getRequestApi(param);

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

    private Api getRequestApi(Map<String, Object> param) {
        String callUrl = StrUtils.getStr(param.get("callUrl"));
//        String bizCd = StrUtils.getStr(param.get("bizCd"));
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
                    .map(f -> {
                        final Object p = param.get(f.getFieldNm());
                        if (p != null) {
                            try {
                                if (f.getDataType().equals(DataType.ARRAY) || f.getDataType().equals(DataType.OBJECT)) {
                                    f.setValue(objectMapper.writeValueAsString(p));
                                } else {
                                    f.setValue(StrUtils.getStr(p));
                                }
                            } catch (JsonProcessingException e) {
                                e.printStackTrace();
                            }
                        }
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

//        Map<String, Object> reqParams = (Map<String, Object>) param.get("reqParams");
//
//        //API 마스터 정보 가져오기
//        return reqParams == null ? getRequestApi(callUrl) : getRequestApi(callUrl, reqParams);
    }

    private void addEvent() {

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

    /**
     * 인증 토큰 쿠키 조회 및 저장
     * 토큰이 있으면 가져와서 리턴하고,
     * 없으면 요청해서 저장
     * @param api
     * @param req
     * @return
     * @throws Exception
     */
    private String getApiAccessToken(Api api, HttpServletRequest req) throws Exception {
        String result = "";
        /**
         * 연계할 api가 bearer 토큰 값이 필요할 경우
         */
        if(api.getAuthInfo() !=null && !api.getAuthInfo().isEmpty()) {

            Cookie cookie = cookieService.getCookie(req, "access_token");
            if(cookie != null) {
                log.trace("access_token {} ", cookie.getValue());
                result = cookie.getValue();
            }

            if(api.getAuthInfo().contains("bearer") && cookie == null) {
                String exApiCallUrl = api.getAuthInfo().split("_")[1];

                Map<String, Object> subParam = new HashMap<>();
                subParam.put("callUrl", exApiCallUrl);
                Api subApi = getRequestApi(subParam);

                ResponseEntity subResponseEntity = apiExecutorFactoryService.execute(subApi);
                log.trace( "###subResponseEntity.getBody() {}", subResponseEntity.getBody());

                Map<String, Object> subBody = (Map<String, Object>) subResponseEntity.getBody();
                /**
                 * api param -> response에 설정한 값임
                 */
                String accessToken = String.valueOf((new HashMap<>((Map) subBody.get("auth"))).get("access_token"));

                log.trace("access_token {}", accessToken);
                result = accessToken;

                /**
                 * 시간은 액세스 토큰 만료 시간 보다 작게 설정
                 */
                cookieService.createCookie(req, "access_token", accessToken, 5 * 60 );
            }
        }

        return result;
    }
}

