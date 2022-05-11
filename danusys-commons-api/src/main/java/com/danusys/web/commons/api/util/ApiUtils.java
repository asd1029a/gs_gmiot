package com.danusys.web.commons.api.util;

import com.danusys.web.commons.api.model.Api;
import com.danusys.web.commons.api.service.ApiCallService;
import com.danusys.web.commons.api.service.ApiExecutorFactoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class ApiUtils {
    private final ApiExecutorFactoryService apiExecutorFactoryService;
    private final ApiCallService apiService;

    public String getRestCallBody(Map<String, Object> param) throws Exception{

        Api api = apiService.getRequestApi(param);
        /**
         * api 요청시 인증 토큰이 필요한 경우
         */
//        apiService.getApiAccessToken(api, request);

        /**
         * API DB 정보로 외부 API 호출
         */
        ResponseEntity responseEntity = apiExecutorFactoryService.execute(api);
        return (String) responseEntity.getBody();
    }
}
