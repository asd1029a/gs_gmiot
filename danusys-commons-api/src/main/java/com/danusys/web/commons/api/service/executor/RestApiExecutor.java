package com.danusys.web.commons.api.service.executor;

import com.danusys.web.commons.api.model.Api;
import com.danusys.web.commons.api.model.ApiParam;
import com.danusys.web.commons.crypto.service.CryptoExecutorFactoryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.DefaultUriBuilderFactory;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.stream.Collectors;


/**
 * Project : danusys-webservice-parent
 * Created by IntelliJ IDEA
 * Developer : kai
 * Date : 2021/11/02
 * Time : 3:31 오후
 */
@Slf4j
@Service("REST")
public class RestApiExecutor implements ApiExecutor {

    private RestTemplate restTemplate;

    private CryptoExecutorFactoryService cryptoExecutorFactoryService;

    public RestApiExecutor(RestTemplate restTemplate, CryptoExecutorFactoryService cryptoExecutorFactoryService) {
        this.restTemplate = restTemplate;
        this.cryptoExecutorFactoryService = cryptoExecutorFactoryService;
    }

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

        // 암호화 모듈 테스트
        apiRequestParams.stream().filter(f -> f.getCryptoKey() != null).forEach(d -> d.setValue(cryptoExecutorFactoryService.encrypt(d.getCryptoType(), d.getValue(), d.getCryptoKey())));
        //요청 파라미터 값 추출
        final Map<String, Object> reqMap = apiRequestParams
                .stream()
                .collect(Collectors.toMap(ApiParam::getFieldMapNm, ApiParam::getValue));
        final String targetUrl = api.getTargetUrl() + api.getTargetPath();
        String result = "";

        try {
//            final HttpHeaders headers = new HttpHeaders();
            HttpMethod method = HttpMethod.valueOf(api.getMethodType().name());
            MediaType mediaType = MediaType.valueOf(api.getContentType());

            log.trace("웹서비스 주소:{}, 메소드:{}, 미디어타입:{}, 파라미터:{}", targetUrl, method, mediaType, reqMap);

            ResponseEntity<String> responseEntity = getResponseEntity(api, method, mediaType, reqMap);

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
            System.out.println(ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("");
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(result);
    }

    private ResponseEntity getResponseEntity(Api api
            , HttpMethod method
            , MediaType mediaType
            , Map<String, Object> reqMap) {
        return this.getResponseEntity(api.getTargetUrl(), api.getTargetPath(), method, mediaType, reqMap, api.getAuthInfo());

    }

    private ResponseEntity getResponseEntity(String targetUrl
            , String targetPath
            , HttpMethod method
            , MediaType mediaType
            , Map<String, Object> reqMap
            , String authInfo) {

        URI uri = null;

        //HttpEntity requestEntity = null;
        ResponseEntity<String> responseEntity = null;

        Mono<ResponseEntity<String>> mono = null;
        WebClient webClient = WebClient.create(targetUrl);

        MultiValueMap<String,String> params = new LinkedMultiValueMap<>();
        String json = "";
        try {
            //final HttpHeaders headers = new HttpHeaders();
            Consumer<HttpHeaders> headersConsumer = httpHeaders -> {
                httpHeaders.setContentType(mediaType);
                httpHeaders.setAccept(Collections.singletonList(mediaType));
                if((!authInfo.isEmpty()) && (authInfo != null)){
                    httpHeaders.set("Authorization", authInfo);
                }
            };

            DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory(targetUrl);
            factory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.NONE);

            ExchangeStrategies exchangeStrategies = ExchangeStrategies
                    .builder()
                    .codecs(configure -> configure.defaultCodecs().maxInMemorySize(1024*1024*50)).build();


            if (method == HttpMethod.GET || method == HttpMethod.DELETE) {
                reqMap.entrySet().stream().forEach(f -> {
                    params.add(f.getKey(), (String) f.getValue());
                });
                URI tUri = factory.builder().queryParams(params).path(targetPath).build();

                mono = WebClient.builder()
//                        .filter(ExchangeFilterFunction.ofRequestProcessor(
//                                clientRequest -> {
//                                    log.debug("Request: {} {}", clientRequest.method(), clientRequest.url());
//                                    clientRequest.headers()
//                                            .forEach((name, values) -> values.forEach(value -> log.debug("{} : {}", name, value)));
//                                    return Mono.just(clientRequest);
//                                }
//                        ))
//                        .filter(ExchangeFilterFunction.ofResponseProcessor(
//                                clientResponse -> {
//                                    clientResponse.headers()
//                                            .asHttpHeaders()
//                                            .forEach((name, values) ->
//                                                    values.forEach(value -> log.debug("{} : {}", name, value)));
//                                    return Mono.just(clientResponse);
//                                }
//                        ))
                        .uriBuilderFactory(factory)
                        .build().method(method)
                        .uri(tUri)
                        .headers(headersConsumer)
                        .exchange()
                        .flatMap(response -> {
                            log.trace("response status code : {}", response.statusCode());

                            return response.toEntity(String.class);
                        });

            } else if (method == HttpMethod.POST || method == HttpMethod.PUT) {
                json = new ObjectMapper().writeValueAsString(reqMap);

                mono = WebClient.builder()
                        .filter(ExchangeFilterFunction.ofRequestProcessor(
                                clientRequest -> {
                                    log.debug("Request: {} {}", clientRequest.method(), clientRequest.url());
                                    clientRequest.headers()
                                            .forEach((name, values) -> values.forEach(value -> log.debug("{} : {}", name, value)));
                                    return Mono.just(clientRequest);
                                }
                        ))
                        .filter(ExchangeFilterFunction.ofResponseProcessor(
                                clientResponse -> {
                                    log.trace("response status code : {}", clientResponse.statusCode());
                                    clientResponse.headers()
                                            .asHttpHeaders()
                                            .forEach((name, values) ->
                                                    values.forEach(value -> log.debug("{} : {}", name, value)));
                                    return Mono.just(clientResponse);
                                }
                        ))
                        .exchangeStrategies(exchangeStrategies)
                        .uriBuilderFactory(factory)
                        .build().method(method)
                        .uri(uriBuilder -> uriBuilder.path(targetPath)
                                .build())
                        .bodyValue(json)
                        .headers(headersConsumer)
                        .exchange()
                        .flatMap(response -> {
                            log.trace("response status code : {}", response.statusCode());

                            return response.toEntity(String.class);
                        });
            }

            responseEntity = mono.block();

        } catch (RestClientResponseException rcrex) {
            return ResponseEntity.status(rcrex.getRawStatusCode()).body("");
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("");
        }
        return responseEntity;
    }

    private ResponseEntity getResponseEntity(String targetUrl, HttpMethod method, MediaType mediaType, Map<String, Object> reqMap) {
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

            responseEntity = restTemplate.exchange(uri, method, requestEntity, String.class);
        } catch (RestClientResponseException rcrex) {
            return ResponseEntity.status(rcrex.getRawStatusCode()).body("");
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("");
        }

        return responseEntity;
    }
}
