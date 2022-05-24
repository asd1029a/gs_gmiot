package com.danusys.web.platform.service.faceDetection;

import com.danusys.web.commons.app.EgovMap;

import java.util.Map;

public interface FaceDetectionService {
    EgovMap getList(Map<String, Object> paramMap) throws Exception;
    EgovMap getListPaging(Map<String, Object> paramMap) throws Exception;
    EgovMap getOne(int seq) throws Exception;
    EgovMap getOne(String name) throws Exception;
    int add(Map<String, Object> paramMap) throws Exception;
    int mod(Map<String, Object> paramMap) throws Exception;
    void del(int seq) throws Exception;
}
