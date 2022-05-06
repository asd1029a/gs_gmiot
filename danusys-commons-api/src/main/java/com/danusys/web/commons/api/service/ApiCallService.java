package com.danusys.web.commons.api.service;

import com.danusys.web.commons.api.model.Api;
import com.danusys.web.commons.api.model.ApiParam;
import com.danusys.web.commons.api.types.DataType;
import com.danusys.web.commons.api.types.ParamType;
import com.danusys.web.commons.app.CamelUtil;
import com.danusys.web.commons.app.StrUtils;
import com.danusys.web.commons.app.service.CookieService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

import static java.util.stream.Collectors.toList;

/**
 * Project : danusys-webservice-parent
 * Created by IntelliJ IDEA
 * Developer : kai
 * Date : 2022/05/06
 * Time : 9:34 AM
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ApiCallService {
    private final CookieService cookieService;
    private final ApiExecutorService apiExecutorService;
    private final ApiExecutorFactoryService apiExecutorFactoryService;
    private final ObjectMapper objectMapper;

    private final HttpServletRequest request;
    private final HttpServletResponse response;

    public Api getRequestApi(Map<String, Object> param) {
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

    /**
     * 인증 토큰 쿠키 조회 및 저장
     * 토큰이 있으면 가져와서 리턴하고,
     * 없으면 요청해서 저장
     * @param api
     * @return
     * @throws Exception
     */
    public String getApiAccessToken(Api api) throws Exception {
        String result = "";
        /**
         * 연계할 api가 bearer 토큰 값이 필요할 경우
         */
        if(api.getAuthInfo() !=null && !api.getAuthInfo().isEmpty()) {

            Cookie cookie = cookieService.getCookie(request, "access_token");
            if(cookie != null) {
                log.trace("getApiAccessToken access_token 조회 {} ", cookie.getValue());
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

                log.trace("getApiAccessToken access_token 저장 {}", accessToken);
                result = accessToken;

                /**
                 * 시간은 액세스 토큰 만료 시간 보다 작게 설정
                 */
                Cookie saveCookie = cookieService.createCookie(request, "access_token", accessToken, 5 * 60 );
                response.addCookie(saveCookie);
            }
        }

        return result;
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
}
