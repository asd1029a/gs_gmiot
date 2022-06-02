package com.danusys.web.commons.app.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;


/**
 * Project : kai-blog
 * Created by IntelliJ IDEA
 * Developer : kai
 * Date : 2022/03/14
 * Time : 12:58 PM
 */
@Slf4j
@Component
public class WebClientHelper {

    /**
     * 파라미터 있고, 액세스토큰 없는 호출
     * @param url
     * @param mediaType
     * @param method
     * @param params
     * @param clazz
     * @param <T>
     * @return
     * @throws Exception
     */
    public <T> T exchange(final String url, MediaType mediaType, HttpMethod method, MultiValueMap<String, Object> params, Class clazz) throws Exception {
        return exchange(url, mediaType, method, params, null, clazz);
    }

    public <T> T exchange(final String url, MediaType mediaType, HttpMethod method, Class clazz) throws Exception {
        return exchange(url, mediaType, method, null, null, clazz);
    }

    /**
     * 파라미터 없고, 액세스토큰 있는 호출
     * @param url
     * @param mediaType
     * @param method
     * @param accessToken
     * @param clazz
     * @param <T>
     * @return
     * @throws Exception
     */
    public <T> T exchange(final String url, MediaType mediaType, HttpMethod method, String accessToken, Class clazz) throws Exception {
        return exchange(url, mediaType, method, null, accessToken, clazz);
    }

    /**
     * restTemplate call
     * @param url
     * @param mediaType
     * @param method
     * @param params
     * @param accessToken
     * @param clazz
     * @param <T>
     * @return
     * @throws Exception
     */
    public <T> T exchange(final String url, MediaType mediaType, HttpMethod method, MultiValueMap<String, Object> params, String accessToken, Class clazz) throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(mediaType);
        headers.add("X-M2M-Origin", "ASN_CSE-D-33da4b19b5-FSTD");
        headers.add("X-M2M-RI", "/IN_CSE-BASE-1/ASN_CSE-D-33da4b19b5-FSTD/ft88946DNy-1649752553089-fw");

        if(accessToken != null && !"".equals(accessToken)) {
            headers.setBearerAuth(accessToken);
        }

        try {
            HttpEntity<MultiValueMap<String, Object>> request = null;
            if (params == null || params.isEmpty()) {
                request = new HttpEntity<>(headers);
            } else {
                request = new HttpEntity<>(params, headers);
            }
            log.trace("request : {}", request);

            final ResponseEntity<?> response = restTemplate.exchange(url, method, request, clazz);
            log.trace("status: {}", response.getStatusCode());

            return (T) response;
        } catch (HttpStatusCodeException e) {
            log.error("error status: {}, message: {}", e.getStatusCode(), e.getMessage());
            throw e;
        }
    }
}