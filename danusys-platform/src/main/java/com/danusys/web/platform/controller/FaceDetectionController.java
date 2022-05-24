package com.danusys.web.platform.controller;

import com.danusys.web.commons.app.CommonUtil;
import com.danusys.web.commons.app.EgovMap;
import com.danusys.web.commons.app.FileUtil;
import com.danusys.web.commons.app.RestUtil;
import com.danusys.web.commons.auth.util.LoginInfoUtil;
import com.danusys.web.platform.dto.request.FaceDetectionRequestDto;
import com.danusys.web.platform.service.faceDetection.FaceDetectionService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping(value = "/faceDetection")
@RequiredArgsConstructor
public class FaceDetectionController {
    private final FaceDetectionService faceDetectionService;

    private String localIp = "127.0.0.1";
    /*@Value("${server.port}")*/
    private int serverPort = 8400;

    /**
     * 얼굴 검출 : 얼굴 목록 조회
     */
    @PostMapping
    public ResponseEntity<EgovMap> getList(@RequestBody Map<String, Object> paramMap) throws Exception {
        return ResponseEntity.ok().body(faceDetectionService.getList(paramMap));
    }

    /**
     * 얼굴 검출 : 얼굴 목록 조회 페이징
     */
    @PostMapping(value = "/paging")
    public ResponseEntity<EgovMap> getListPaging(@RequestBody Map<String, Object> paramMap) throws Exception {
        return ResponseEntity.ok().body(faceDetectionService.getListPaging(paramMap));
    }

    /**
     * 얼굴 검출 : 얼굴 조회
     */
    @GetMapping(value = "/{faceSeq}")
    public ResponseEntity<EgovMap> getOne(@PathVariable("faceSeq") int faceSeq) throws Exception {
        return ResponseEntity.ok().body(faceDetectionService.getOne(faceSeq));
    }

    /**
     * 얼굴 검출 : 얼굴 조회
     */
    @GetMapping(value = "/checkName/{faceName}")
    public ResponseEntity<EgovMap> getOne(@PathVariable("faceName") String faceName) throws Exception {
        return ResponseEntity.ok().body(faceDetectionService.getOne(faceName));
    }

    /**
     * 얼굴 검출 : 얼굴 등록
     */
    @PostMapping(value = "/add", produces = "multipart/form-data")
    public ResponseEntity<?> add(MultipartFile[] file, HttpServletRequest request, FaceDetectionRequestDto faceDetectionRequestDto) throws Exception {

        String fileName = FileUtil.uploadAjaxPost(file, request);

        if (!fileName.equals("")) {
            faceDetectionRequestDto.setFaceFile(fileName);

            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> paramMap = objectMapper.convertValue(faceDetectionRequestDto, Map.class);
            paramMap.put("insertUserSeq", LoginInfoUtil.getUserDetails().getUserSeq());
            paramMap.put("insertDt", Timestamp.valueOf(LocalDateTime.now()));

            try {
                String apiKey = this.getIntellivixApiKey();
                // TODO: header param mapping
                Map<String, Object> headerMap = new HashMap<>();
                headerMap.put("api-key", apiKey);

                Map<String, Object> param = new HashMap<>();
                param.put("callUrl", "/intellivix/faceAdd");
                param.put("file", file);
                param.putAll(paramMap);


                ResponseEntity responseEntity = RestUtil.exchange("http://" + localIp + ":" + serverPort + "/api/call", HttpMethod.POST, MediaType.MULTIPART_FORM_DATA, param, headerMap);

                if (responseEntity.getStatusCodeValue() != 200) {
                    log.debug("error face register");
                    // TODO 연계되면 에러 내리기
//                    throw new Exception();
                }
                faceDetectionService.add(paramMap);
                return ResponseEntity.noContent().build();
            } catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.badRequest().build();
            }
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 얼굴 검출 : 얼굴 수정
     */
    @PostMapping(value = "/mod/{faceSeq}", produces = "multipart/form-data")
    public ResponseEntity<?> mod(MultipartFile[] file, HttpServletRequest request, @PathVariable("faceSeq") int faceSeq, FaceDetectionRequestDto faceDetectionRequestDto) throws Exception {

        try {
            String fileName = FileUtil.uploadAjaxPost(file, request);

            if (!fileName.equals("")) {
                faceDetectionRequestDto.setFaceFile(fileName);

                ObjectMapper objectMapper = new ObjectMapper();
                Map<String, Object> paramMap = objectMapper.convertValue(faceDetectionRequestDto, Map.class);
                paramMap.put("faceSeq", faceSeq);
                paramMap.put("updateUserSeq", LoginInfoUtil.getUserDetails().getUserSeq());
                paramMap.put("updateDt", Timestamp.valueOf(LocalDateTime.now()));

                String apiKey = this.getIntellivixApiKey();
                // TODO: header param mapping
                Map<String, Object> headerMap = new HashMap<>();
                headerMap.put("api-key", apiKey);

                Map<String, Object> param = new HashMap<>();
                param.put("callUrl", "/intellivix/faceAdd");
                param.put("file", file);
                param.putAll(paramMap);

                ResponseEntity responseEntity = RestUtil.exchange("http://" + localIp + ":" + serverPort + "/api/call", HttpMethod.POST, MediaType.MULTIPART_FORM_DATA, param, headerMap);

                if (responseEntity.getStatusCodeValue() != 200) {
                    log.debug("error face register");
                    // TODO 연계되면 에러 내리기
//                    throw new Exception();
                }
                faceDetectionService.mod(paramMap);
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.badRequest().build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 얼굴 검출 : 얼굴 삭제
     */
    @DeleteMapping(value = "/{faceSeq}")
    public ResponseEntity<?> del(@PathVariable("faceSeq") int faceSeq) throws Exception {
        String apiKey = this.getIntellivixApiKey();
        // TODO: header param mapping
        Map<String, Object> headerMap = new HashMap<>();
        headerMap.put("api-key", apiKey);

        Map<String, Object> faceData = faceDetectionService.getOne(faceSeq);

        Map<String, Object> param = new HashMap<>();
        param.put("callUrl", "/intellivix/faceDel");
        param.put("faceName", CommonUtil.validOneNull(faceData, "faceName"));

        ResponseEntity responseEntity = RestUtil.exchange("http://" + localIp + ":" + serverPort + "/api/call", HttpMethod.POST, MediaType.APPLICATION_JSON, param, headerMap);

        if (responseEntity.getStatusCodeValue() != 200) {
            log.debug("error face register");
            // TODO 연계되면 에러 내리기
//                    throw new Exception();
        }
        faceDetectionService.del(faceSeq);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * 얼굴 검출 : 얼굴 목록 엑셀 다운로드
     */
    @ResponseBody
    @PostMapping(value = "/excel/download")
    public void exportNotice(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, Object> paramMap) throws Exception {
        Map<String, Object> dataMap = faceDetectionService.getList((Map<String, Object>) paramMap.get("search"));

        paramMap.put("dataMap", dataMap.get("data"));
        Workbook wb = FileUtil.excelDownload(paramMap);
        wb.write(response.getOutputStream());
        wb.close();
    }

    private String getIntellivixApiKey() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> param = new HashMap<>();
        param.put("callUrl", "/intellivix/usersLogin");
        ResponseEntity responseEntity = RestUtil.exchange("http://" + localIp + ":" + serverPort + "/api/call", HttpMethod.POST, MediaType.APPLICATION_JSON, param);
        if (responseEntity.getStatusCodeValue() != 200) {
            log.debug("error api-key");
            throw new Exception();
        }
        String body = responseEntity.getBody().toString();
        Map<String, Object> resultBody = objectMapper.readValue(body, new TypeReference<Map<String, Object>>() {});
        return CommonUtil.validOneNull(resultBody, "api-key");
    }
}
