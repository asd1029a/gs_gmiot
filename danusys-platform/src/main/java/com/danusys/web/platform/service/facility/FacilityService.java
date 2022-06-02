package com.danusys.web.platform.service.facility;

import com.danusys.web.commons.app.EgovMap;
import com.danusys.web.platform.dto.request.SignageRequestDto;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

public interface FacilityService {
    EgovMap getList(Map<String, Object> paramMap) throws Exception;
    EgovMap getListPaging(Map<String, Object> paramMap) throws Exception;
    EgovMap getOne(int seq) throws Exception;
    int add(Map<String, Object> paramMap) throws Exception;
    int addOpt(Map<String, Object> paramMap) throws Exception;
    int mod(Map<String, Object> paramMap) throws Exception;
    int modOpt(Map<String, Object> paramMap) throws Exception;
    void del(int seq) throws Exception;
    void delOpt(Map<String, Object> paramMap) throws Exception;
    EgovMap getListFacilityInStation(Map<String, Object> paramMap) throws Exception;
    EgovMap getListDimmingGroup(Map<String, Object> paramMap) throws Exception;
    EgovMap getLastDimmingGroupSeq() throws Exception;
    EgovMap getListLampRoadInDimmingGroup(Map<String, Object> paramMap) throws Exception;
    EgovMap getListSignageTemplate(Map<String, Object> paramMap) throws Exception;
    EgovMap getSignageLayout(int id) throws Exception;
    int addSignageTemplate(Map<String, Object> paramMap) throws Exception;
    int modSignageTemplate(Map<String, Object> paramMap) throws Exception;
    void modSignageLayout(MultipartFile[] imageFile, MultipartFile[] videoFile
            , HttpServletRequest request, SignageRequestDto signageRequestDto) throws Exception;
    void modSignageLayoutForGm(Map<String, Object> paramMap) throws Exception ;
    String getOneSignageData() throws Exception;
    void delSignageTemplate(Map<String, Object> paramMap) throws Exception;

}
