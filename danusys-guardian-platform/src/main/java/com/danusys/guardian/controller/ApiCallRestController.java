package com.danusys.guardian.controller;

import com.danusys.guardian.common.util.CamelUtil;
import com.danusys.guardian.common.util.StrUtils;
import com.danusys.guardian.model.Api;
import com.danusys.guardian.model.ApiParam;
import com.danusys.guardian.service.api.ApiService;
import com.danusys.guardian.service.base.BaseService;
import com.danusys.guardian.type.ParamType;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Project : danusys-guardian-parent
 * Created by IntelliJ IDEA
 * Developer : kai
 * Date : 2021/11/03
 * Time : 2:20 오후
 */
@Slf4j
@RestController
public class ApiCallRestController {

    private ApiService apiService;
    private BaseService baseService;
//    private SOAPConnector soapConnector;

    public ApiCallRestController(ApiService apiService, BaseService baseService) {
        this.apiService = apiService;
        this.baseService = baseService;
    }

    @PostMapping(value = "/api/call")
    public ResponseEntity callTest(@RequestBody Map<String, Object> param) throws Exception {

        log.trace("param {}", param.toString());

        Api api = new Api();
        List<ApiParam> requestApiParam = null;
        List<ApiParam> responseApiParam = null;
        try {
            //API 마스터 정보 가져오기
            api = (Api) baseService.baseSelectOneObject("api.selectApi", param);
            //req, res 정보 가져오기
            List<ApiParam> apiParams = baseService.baseSelectList("api.selectApiParam", ImmutableMap.of("apiId", api.getId()));

//            log.trace("apiParams : {}", apiParams.toString());

            //요청 컬럼 정보 가져오기
            requestApiParam = apiParams.stream().filter(f -> f.getParamType() == ParamType.REQUEST).collect(Collectors.toList()); //REQUEST정보 추출
            requestApiParam.stream().map(m -> this.apiRequestSetValue(m, param) ).collect(Collectors.toList()); //파라미터로 입력 받은 값을 request 정보에 세팅
            api.setApiRequestParams(requestApiParam);

            //응답 컬럼 정보 가져오기
            responseApiParam = apiParams.stream().filter(f -> f.getParamType() == ParamType.RESPONSE).collect(Collectors.toList()); //RESPONSE정보 추출
            api.setApiResponseParams(responseApiParam);

        } catch (Exception ex) {
            log.error(ex.toString());
            throw ex;
        }

        //API DB 정보로 외부 API 호출
        ResponseEntity responseEntity = apiService.execute(api);
        String body = (String) responseEntity.getBody();
//        log.trace("### body : {}", body.toString());
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> resultBody = objectMapper.readValue(body, new TypeReference<Map<String, Object>>(){});

        return ResponseEntity.status(HttpStatus.OK)
                .body(resultBody);
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

    @PostMapping("/api/deviceInfoList.json")
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

