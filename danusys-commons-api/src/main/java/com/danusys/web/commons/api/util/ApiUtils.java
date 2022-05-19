package com.danusys.web.commons.api.util;

import com.danusys.web.commons.api.model.Api;
import com.danusys.web.commons.api.service.ApiCallService;
import com.danusys.web.commons.api.service.ApiExecutorFactoryService;
import com.danusys.web.commons.api.types.BodyType;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ApiUtils {
    private final ObjectMapper objectMapper;
    private final ApiExecutorFactoryService apiExecutorFactoryService;
    private final ApiCallService apiService;

    public Object getRestCallBody(Map<String, Object> param) throws Exception{
        Object result = null;

        Api api = apiService.getRequestApi(param);
        /**
         * api 요청시 인증 토큰이 필요한 경우
         */
//        apiService.getApiAccessToken(api, request);

        /**
         * API DB 정보로 외부 API 호출
         */
        ResponseEntity responseEntity = apiExecutorFactoryService.execute(api);

        if (responseEntity.getStatusCode() != HttpStatus.OK) {
            return result;
        }

        String body = (String) responseEntity.getBody();

        if (api.getResponseBodyType() == BodyType.OBJECT) {
            result = objectMapper.readValue(body, new TypeReference<Map<String, Object>>() {
            });
        } else if (api.getResponseBodyType() == BodyType.ARRAY) {
            result = objectMapper.readValue(body, new TypeReference<List<Map<String, Object>>>() {
            });
        } else {
            result = body;
        }

        return result;
    }
}
