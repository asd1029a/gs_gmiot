package com.danusys.web.platform.service.faceDetection;

import com.danusys.web.commons.app.EgovMap;
import com.danusys.web.platform.dto.request.FaceDetectionRequestDto;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public interface FaceDetectionService {
    EgovMap getList(Map<String, Object> paramMap) throws Exception;
    EgovMap getListPaging(Map<String, Object> paramMap) throws Exception;
    EgovMap getOne(int seq) throws Exception;
    EgovMap getOne(String name) throws Exception;

    @Transactional(rollbackFor = Exception.class)
    int add(MultipartFile[] file, HttpServletRequest request, FaceDetectionRequestDto faceDetectionRequestDto) throws Exception;

    @Transactional(rollbackFor = Exception.class)
    int mod(MultipartFile[] file, HttpServletRequest request, int faceSeq, FaceDetectionRequestDto faceDetectionRequestDto) throws Exception;

    void del(int seq) throws Exception;
}
