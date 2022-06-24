package com.danusys.web.platform.service.faceDetection;

import com.danusys.web.commons.app.*;
import com.danusys.web.commons.auth.util.LoginInfoUtil;
import com.danusys.web.platform.dto.request.FaceDetectionRequestDto;
import com.danusys.web.platform.mapper.common.CommonMapper;
import com.danusys.web.platform.mapper.faceDetection.FaceDetectionSqlProvider;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class FaceDetectionServiceImpl implements FaceDetectionService {
    private final CommonMapper commonMapper;
    private final FaceDetectionSqlProvider fdsp = new FaceDetectionSqlProvider();

    private String localIp = "127.0.0.1";
    /*@Value("${server.port}")*/
    private int serverPort = 8400;

    @Override
    public EgovMap getList(Map<String, Object> paramMap) throws Exception {
        EgovMap resultMap = new EgovMap();
        resultMap.put("data", commonMapper.selectList(fdsp.selectListQry(paramMap)));
        return resultMap;
    }

    @Override
    public EgovMap getListPaging(Map<String, Object> paramMap) throws Exception {
        Map<String, Object> pagingMap = new HashMap<>();
        EgovMap count = commonMapper.selectOne(fdsp.selectCountQry(paramMap));
        pagingMap.put("data", commonMapper.selectList(fdsp.selectListQry(paramMap)));
        pagingMap.put("count", count.get("count"));
        return PagingUtil.createPagingMap(paramMap, pagingMap);
    }

    @Override
    public EgovMap getOne(int seq) throws Exception {
        return commonMapper.selectOne(fdsp.selectOneQry(seq));
    }

    @Override
    public EgovMap getOne(String name) throws Exception {
        return commonMapper.selectOne(fdsp.selectOneNameQry(name));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int add(MultipartFile[] file, HttpServletRequest request, FaceDetectionRequestDto faceDetectionRequestDto) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> paramMap = objectMapper.convertValue(faceDetectionRequestDto, Map.class);
        paramMap.put("faceUid", CommonUtil.getUniqueID());

        try {
            Map<String, Object> apiParam = new HashMap<>();
            apiParam.put("id", CommonUtil.validOneNull(paramMap, "faceUid"));
            apiParam.put("name", CommonUtil.validOneNull(paramMap, "faceName"));
            apiParam.put("age", Integer.parseInt(CommonUtil.validOneNull(paramMap, "faceAge")));
            apiParam.put("gender", Integer.parseInt(CommonUtil.validOneNull(paramMap, "faceGender")));
            apiParam.put("imgNum", 0);

            MultipartFile faceImg = file[0];
            if (!faceImg.isEmpty()) {
                apiParam.put("imgNum", 1);
                apiParam.put("imgLen", faceImg.getSize());
                apiParam.put("img", FileUtil.multiFileToBase64(faceImg));
                apiParam.put("subImg", new ArrayList<>());

                String fileName = FileUtil.uploadAjaxPost(file, request);
                paramMap.put("faceFile", fileName);
            }

            Map<String, Object> headerMap = new HashMap<>();
            headerMap.put("api-key", this.getIntellivixApiKey());

            ResponseEntity responseEntity = RestUtil.exchange("http://1.213.164.187:5204/faces", HttpMethod.POST, MediaType.APPLICATION_JSON, apiParam, headerMap);

            if (responseEntity.getStatusCodeValue() != 200) {
                log.debug("error face register");
                throw new Exception();
            }

            paramMap.put("insertUserSeq", LoginInfoUtil.getUserDetails().getUserSeq());
            paramMap.put("insertDt", Timestamp.valueOf(LocalDateTime.now()));
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception();
        }

        return commonMapper.insert(fdsp.insertQry(paramMap));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int mod(MultipartFile[] file, HttpServletRequest request, int faceSeq, FaceDetectionRequestDto faceDetectionRequestDto) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> paramMap = objectMapper.convertValue(faceDetectionRequestDto, Map.class);

        try {
            Map<String, Object> apiParam = new HashMap<>();
            apiParam.put("id", CommonUtil.validOneNull(paramMap, "faceUid"));
            apiParam.put("name", CommonUtil.validOneNull(paramMap, "faceName"));
            apiParam.put("age", Integer.parseInt(CommonUtil.validOneNull(paramMap, "faceAge")));
            apiParam.put("gender", Integer.parseInt(CommonUtil.validOneNull(paramMap, "faceGender")));
            apiParam.put("imgNum", 0);

            MultipartFile faceImg = file[0];
            if (!faceImg.isEmpty()) {
                apiParam.put("imgNum", 1);
                apiParam.put("imgLen", faceImg.getSize());
                apiParam.put("img", FileUtil.multiFileToBase64(faceImg));
                apiParam.put("subImg", new ArrayList<>());

                String fileName = FileUtil.uploadAjaxPost(file, request);
                paramMap.put("faceFile", fileName);
            }

            Map<String, Object> headerMap = new HashMap<>();
            headerMap.put("api-key", this.getIntellivixApiKey());

            ResponseEntity responseEntity = RestUtil.exchange("http://1.213.164.187:5204/faces", HttpMethod.PUT, MediaType.APPLICATION_JSON, apiParam, headerMap);

            if (responseEntity.getStatusCodeValue() != 200) {
                log.debug("error face register");
                throw new Exception();
            }

            paramMap.put("faceSeq", faceSeq);
            paramMap.put("updateUserSeq", LoginInfoUtil.getUserDetails().getUserSeq());
            paramMap.put("updateDt", Timestamp.valueOf(LocalDateTime.now()));
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception();
        }

        return commonMapper.update(fdsp.updateQry(paramMap));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void del(int seq) throws Exception {

        Map<String, Object> faceData = this.getOne(seq);

        Map<String, Object> param = new HashMap<>();
        param.put("name", CommonUtil.validOneNull(faceData, "faceName"));
        param.put("id", CommonUtil.validOneNull(faceData, "faceUid"));

        try {
            RestTemplate restTemplate = new RestTemplate();
            String apiKey = this.getIntellivixApiKey();

            final HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("api-key", apiKey);

            final String json = new ObjectMapper().writeValueAsString(param);
            URI uri = new URI("http://1.213.164.187:5204/faces");
            HttpEntity requestEntity = new HttpEntity(json, headers);

            log.trace("restUrl:{}, method:{}, request:{}", "http://1.213.164.187:5204/faces", HttpMethod.DELETE, requestEntity);

            ResponseEntity<String> responseEntity = restTemplate.exchange(uri, HttpMethod.DELETE, requestEntity, String.class);
            if (responseEntity.getStatusCodeValue() != 200) {
                log.debug("error face register");
                throw new Exception();
            }

            commonMapper.delete(fdsp.delete(seq));
        } catch (RestClientResponseException rcrex) {
            ResponseEntity.status(rcrex.getRawStatusCode()).body("");
            log.debug("error face register");
            throw new Exception();
        } catch (Exception ex) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("");
            log.debug("error face register");
            throw new Exception();
        }
    }

    private String getIntellivixApiKey() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> param = new HashMap<>();
        param.put("callUrl", "/intellivix/faceLogin");
        ResponseEntity responseEntity = RestUtil.exchange("http://" + localIp + ":" + serverPort + "/api/call", HttpMethod.POST, MediaType.APPLICATION_JSON, param);
        if (responseEntity.getStatusCodeValue() != 200) {
            log.debug("error api-key");
            throw new Exception();
        }
        String body = responseEntity.getBody().toString();
        Map<String, Object> resultBody = objectMapper.readValue(body, new TypeReference<Map<String, Object>>() {
        });
        return CommonUtil.validOneNull(resultBody, "apiKey");
    }
}