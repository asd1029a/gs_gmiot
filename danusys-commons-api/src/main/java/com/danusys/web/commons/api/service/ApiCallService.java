package com.danusys.web.commons.api.service;

import com.danusys.web.commons.api.model.Api;
import com.danusys.web.commons.api.model.ApiParam;
import com.danusys.web.commons.api.types.BodyType;
import com.danusys.web.commons.api.types.DataType;
import com.danusys.web.commons.api.types.ParamType;
import com.danusys.web.commons.app.CamelUtil;
import com.danusys.web.commons.app.StrUtils;
import com.danusys.web.commons.app.service.CookieManageService;
import com.danusys.web.commons.app.service.CookieService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    private final CookieManageService cookieManageService;
    private final ApiExecutorService apiExecutorService;
    private final ApiExecutorFactoryService apiExecutorFactoryService;
    private final ObjectMapper objectMapper;

    private final HttpServletRequest request;
    private final HttpServletResponse response;


    public ResponseEntity call(Map<String, Object> param) throws Exception {
        log.trace("param {}", param.toString());
        Api api = this.getRequestApi(param);

        /**
         * api ????????? ?????? ????????? ????????? ??????
         */
//        apiService.getApiAccessToken(api, request);

        /**
         * API DB ????????? ?????? API ??????
         */
        ResponseEntity responseEntity = apiExecutorFactoryService.execute(api);

        Object resultBody = null;
        if (api.getResponseBodyType() == BodyType.OBJECT_MAPPING) {
            resultBody = responseEntity.getBody();
        } else if (api.getResponseBodyType() == BodyType.ARRAY) {
            String body = (String) responseEntity.getBody();
            resultBody = body.isEmpty() ? "" : objectMapper.readValue(body, new TypeReference<List<Map<String, Object>>>() {
            });
        } else if (api.getResponseBodyType() == BodyType.OBJECT) {
            String body = (String) responseEntity.getBody();
            resultBody = body.isEmpty() ? "" : objectMapper.readValue(body, new TypeReference<Map<String, Object>>() {
            });
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(resultBody);
    }

    public Api getRequestApi(Map<String, Object> param) {
        String callUrl = StrUtils.getStr(param.get("callUrl"));
//        String bizCd = StrUtils.getStr(param.get("bizCd"));
        Api api = null;

        try {
            log.trace("callUrl : {}", callUrl);

            //API ????????? ?????? ????????????
            api = apiExecutorService.findByCallUrl(StrUtils.getStr(callUrl));

            //?????? ?????? ?????? ????????????
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

            //?????? ?????? ?????? ????????????
            api.setApiResponseParams(apiExecutorService.findApiParam(api.getId(), ParamType.RESPONSE));

        } catch (Exception ex) {
            log.error(ex.toString());
            throw ex;
        }
        return api;

//        Map<String, Object> reqParams = (Map<String, Object>) param.get("reqParams");
//
//        //API ????????? ?????? ????????????
//        return reqParams == null ? getRequestApi(callUrl) : getRequestApi(callUrl, reqParams);
    }

    /**
     * ?????? ?????? ?????? ?????? ??? ??????
     * ????????? ????????? ???????????? ????????????,
     * ????????? ???????????? ??????
     * @param api
     * @return
     * @throws Exception
     */
    public String getApiAccessToken(Api api) throws Exception {
        String result = "";
        /**
         * ????????? api??? bearer ?????? ?????? ????????? ??????
         */
        if(api.getAuthInfo() !=null && !api.getAuthInfo().isEmpty()) {

//            Cookie cookie = cookieService.getCookie(request, "access_token");
            Cookie cookie = cookieManageService.getCookie("access_token");
            if(cookie != null) {
                log.trace("getApiAccessToken access_token ?????? {} ", cookie.getValue());
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
                 * api param -> response??? ????????? ??????
                 */
                String accessToken = String.valueOf((new HashMap<>((Map) subBody.get("auth"))).get("access_token"));

                log.trace("getApiAccessToken access_token ?????? {}", accessToken);
                result = accessToken;

                /**
                 * ????????? ????????? ?????? ?????? ?????? ?????? ?????? ??????
                 */
                log.trace("sessionId : {}", request.getSession().getId());
                // Cookie saveCookie = cookieService.createCookie(request, "access_token", accessToken, 1 * 60 );
                // response.addCookie(saveCookie);
                Cookie saveCookie = cookieManageService.createCookie("access_token", accessToken, 5 * 60);
            }
        }

        return result;
    }

    /**
     * ?????? ?????? ?????? ??? ??????
     * ????????? ????????? ???????????? ????????????,
     * ????????? ???????????? ??????
     * @param api
     * @return
     * @throws Exception
     */
    public Cookie getApiSession(Api api) throws Exception {
        String cookieNames = api.getTokens();
        String[] names = cookieNames.split(",");
        /**
         * ????????? api??? bearer ?????? ?????? ????????? ??????
         */
        Cookie cookie = null;
        if (api.getAuthInfo() != null && !api.getAuthInfo().isEmpty()) {

//            cookie = cookieService.getCookie(request, "Cookie");
            cookie = cookieManageService.getCookie("Cookie");
            if (cookie != null) {
                log.trace("getApiSession session_id ?????? {} ", cookie);
                return cookie;
            }

            if (cookie == null && api.getAuthInfo().contains("session")) {
                String exApiCallUrl = api.getAuthInfo().split("_")[1];

                Map<String, Object> subParam = new HashMap<>();
                subParam.put("callUrl", exApiCallUrl);
                Api subApi = getRequestApi(subParam);

                ResponseEntity subResponseEntity = apiExecutorFactoryService.execute(subApi);
                log.trace("###subResponseEntity.getBody() {}", subResponseEntity.getBody());

                List<String> list = subResponseEntity.getHeaders().get(HttpHeaders.SET_COOKIE);

                List<String> strArray = new ArrayList<>();

                /**
                 * ????????? ????????? ?????? ?????? ?????? ?????? ?????? ??????
                 */
                list.forEach(f -> {
                    String[] temp = f.split(";");
                    strArray.add(temp[0]);
                });

                String result = strArray.stream().collect(Collectors.joining("; "));

                // cookie = cookieService.createCookie(request, "Cookie", result, 5 * 60);
                cookie = cookieManageService.createCookie("Cookie", result, 5 * 60);
            }
        }

        return cookie;
    }


    /**
     * soap Login Client Id
     * @param api
     * @return
     * @throws Exception
     */
    public Integer getSoapClientId(Api api) throws Exception {
        Integer result = null;
        /**
         * ????????? api??? soplogin ??? client Id ?????? ????????? ??????
         */
        if(api.getAuthInfo() !=null && !api.getAuthInfo().isEmpty()) {

            // Cookie cookie = cookieService.getCookie(request, "clientId");
            Cookie cookie = cookieManageService.getCookie("clientId");
            if(cookie != null) {
                log.trace("getSoapClientId client_id ?????? {} ", cookie.getValue());
                result = Integer.parseInt(cookie.getValue());
            }

            if(api.getAuthInfo().contains("soplogin") && cookie == null) {
                String exApiCallUrl = api.getAuthInfo().split("_")[1];

                Map<String, Object> subParam = new HashMap<>();
                subParam.put("callUrl", exApiCallUrl);
                Api subApi = getRequestApi(subParam);

                ResponseEntity subResponseEntity = apiExecutorFactoryService.execute(subApi);
                log.trace( "###subResponseEntity.getBody() {}", subResponseEntity.getBody());

                Map<String, Object> subBody = (Map<String, Object>) subResponseEntity.getBody();
                log.trace( "###subBody {}", subBody);

                /**
                 * api param -> response??? ????????? ??????
                 */
                result = Integer.parseInt(String.valueOf(new HashMap<>((Map) subBody.get("return")).get("clientId")));

                log.trace("cookie clientId ?????? {}", result);


                /**
                 * ????????? clientId ?????? ?????? ?????? ?????? ??????
                 */
                // Cookie saveCookie = cookieService.createCookie(request, "clientId", String.valueOf(result), 20 * 60 );
                // response.addCookie(saveCookie);
                Cookie saveCookie = cookieManageService.createCookie("clientId", String.valueOf(result), 20 * 60 );
            }
        }

        return result;
    }

    /**
     * ??????????????? API Object??? ??????
     * @param apiParam
     * @param param
     * @return
     */
    private ApiParam apiRequestSetValue(ApiParam apiParam, Map<String, Object> param) {
        log.trace("### ?????? {} => {} = {}", apiParam.getFieldNm(), apiParam.getFieldMapNm(), param.get(CamelUtil.convert2CamelCase(apiParam.getFieldNm())));
        apiParam.setValue(StrUtils.getStr(param.get(CamelUtil.convert2CamelCase(apiParam.getFieldNm()))));
        return apiParam;
    }

    /**
     * ?????? ????????? ??? ??????
     * @param cookieName
     * @return
     */
    public String getCookie(String cookieName) {
        Cookie cookie = cookieManageService.getCookie(cookieName);
        if (cookie == null) return "";

        log.trace("### getCookie {} = {}", cookieName, cookie.getValue());
        return cookie.getValue();
    }

}
