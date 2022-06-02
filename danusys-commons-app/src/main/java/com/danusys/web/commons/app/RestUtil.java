package com.danusys.web.commons.app;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.formula.functions.T;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class RestUtil {

    public static ResponseEntity exchange(String targetUrl, HttpMethod method, MediaType mediaType, Map<String, Object> reqMap) {
        return exchange(targetUrl, method, mediaType, reqMap, String.class);
    }

    public static ResponseEntity exchange(String targetUrl, HttpMethod method, MediaType mediaType, Map<String, Object> reqMap, Class clazz) {
        RestTemplate restTemplate = new RestTemplate();
        URI uri = null;

        HttpEntity requestEntity = null;
        ResponseEntity<String> responseEntity = null;

        try {
            final HttpHeaders headers = new HttpHeaders();
            headers.setContentType(mediaType);
            headers.setAccept(Collections.singletonList(mediaType));

            if (method == HttpMethod.GET || method == HttpMethod.DELETE) {
                String queryString = reqMap.entrySet()
                        .stream().map(f -> {
                            return f.getKey() + "=" + f.getValue() + "";
                        })
                        .collect(Collectors.joining("&"));

                uri = new URI(targetUrl + "?" + queryString);
            } else if (method == HttpMethod.POST || method == HttpMethod.PUT) {
                final String json = new ObjectMapper().writeValueAsString(reqMap);
                uri = new URI(targetUrl);
                requestEntity = new HttpEntity(json, headers);
            }

            log.trace("restUrl:{}, method:{}, request:{}", targetUrl, method, requestEntity);

            responseEntity = restTemplate.exchange(uri, method, requestEntity, clazz);
        } catch (RestClientResponseException rcrex) {
            return ResponseEntity.status(rcrex.getRawStatusCode()).body("");
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("");
        }

        return responseEntity;
    }

    public static ResponseEntity exchange(String targetUrl, HttpMethod method, MediaType mediaType, Map<String, Object> reqMap, Map<String, Object> headerMap) {
        RestTemplate restTemplate = new RestTemplate();
        URI uri = null;

        HttpEntity requestEntity = null;
        ResponseEntity<?> responseEntity = null;

        try {
            final HttpHeaders headers = new HttpHeaders();
            headers.setContentType(mediaType);
            headers.setAccept(Collections.singletonList(mediaType));
            headerMap.forEach((k, v) -> {
                headers.add(k, CommonUtil.validNull(v));
            });

            if (mediaType.getType().equals("multipart")) {
                MultiValueMap<String, Object> body = new  LinkedMultiValueMap<>();
                body.setAll(reqMap);
                requestEntity = new HttpEntity(body, headers);
                uri = new URI(targetUrl);
            } else if (method == HttpMethod.GET || method == HttpMethod.DELETE) {
                String queryString = reqMap.entrySet()
                        .stream().map(f -> {
                            return f.getKey() + "=" + f.getValue() + "";
                        })
                        .collect(Collectors.joining("&"));

                uri = new URI(targetUrl + "?" + queryString);
            } else if (method == HttpMethod.POST || method == HttpMethod.PUT) {
                final String json = new ObjectMapper().writeValueAsString(reqMap);
                uri = new URI(targetUrl);
                requestEntity = new HttpEntity(json, headers);
            }

            log.trace("restUrl:{}, method:{}, request:{}", targetUrl, method, requestEntity);

            responseEntity = restTemplate.exchange(uri, method, requestEntity, String.class);
        } catch (RestClientResponseException rcrex) {
            return ResponseEntity.status(rcrex.getRawStatusCode()).body("");
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("");
        }

        return responseEntity;
    }
}
