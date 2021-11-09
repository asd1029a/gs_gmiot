package com.danusys.guardian.service.executor;

import com.danusys.guardian.model.Api;
import com.danusys.guardian.model.ApiParam;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;


/**
 * Project : danusys-guardian-parent
 * Created by IntelliJ IDEA
 * Developer : kai
 * Date : 2021/11/02
 * Time : 3:31 오후
 */
@Slf4j
@Service("REST")
public class RestApiExecutor implements ApiExecutor {
//    @Autowired
//    private ObjectMapper objectMapper;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public ResponseEntity execute(final Api api) throws Exception {
        if (api == null)
            throw new IllegalArgumentException("입력된 API 파라미터 값이 null 입니다.");
        if (api.getCallUrl() == null)
            throw new IllegalArgumentException("호출 URL 값이 null 입니다.");
        if (api.getApiRequestParams() == null || api.getApiRequestParams().isEmpty())
            throw new IllegalArgumentException("apiRequestParams 값이 null 입니다.");

        final List<ApiParam> apiRequestParams = api.getApiRequestParams().stream().sorted(Comparator.comparing((ApiParam p) -> p.getSeq())).collect(Collectors.toList());
        log.trace("### apiRequestParams : {}", apiRequestParams.toString());

        //TODO IN / OUT 로그 저장 ??
        //TODO REST API 인증키
        //TODO 암호화?

        //요청 파라미터 값 추출
        final Map<String, Object> reqMap = apiRequestParams.stream().collect(Collectors.toMap(ApiParam::getFieldMapNm, ApiParam::getValue));
        final String targetUrl = api.getTargetUrl() + api.getTargetPath();
        String result = "";

        try {
            final HttpHeaders headers = new HttpHeaders();
            HttpMethod method = HttpMethod.valueOf(api.getMethodType().name());
            MediaType mediaType = MediaType.valueOf(api.getContentType());

            headers.setContentType(mediaType);
            headers.setAccept(Collections.singletonList(mediaType));
            log.trace("웹서비스 주소:{}, 메소드:{}, 미디어타입:{}, 파라미터:{}", targetUrl, method, mediaType, reqMap);

            HttpEntity requestEntity = null;
            if (method == HttpMethod.GET || method == HttpMethod.DELETE) {

            } else if (method == HttpMethod.POST || method == HttpMethod.PUT) {
                final String json = new ObjectMapper().writeValueAsString(reqMap);
                requestEntity = new HttpEntity(json, headers);
            }

            log.trace("restUrl:{}, method:{}, request:{}", targetUrl, method, requestEntity);
            ResponseEntity<String> responseEntity = restTemplate.exchange(targetUrl, method, requestEntity, String.class);
            final String res = responseEntity.getBody();

            AtomicReference<String> body = new AtomicReference(res);
            api.getApiResponseParams().forEach(apiRes -> {
                log.trace("### 응답 {} => {}", apiRes.getFieldMapNm(), apiRes.getFieldNm());
                body.set(StringUtils.replace(body.get(), apiRes.getFieldMapNm(), apiRes.getFieldNm()));
            });
//            log.trace("convert body:{}", body);
            result = body.get();


        } catch (RestClientResponseException rcrex) {
            return ResponseEntity.status(rcrex.getRawStatusCode()).body("");
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("");
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(result);
     }
}
