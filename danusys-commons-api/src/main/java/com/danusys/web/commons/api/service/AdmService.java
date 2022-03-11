package com.danusys.web.commons.api.service;

import com.danusys.web.commons.api.model.AdmInfo;
import com.danusys.web.commons.api.repository.AdmInfoRepository;

import com.danusys.web.commons.app.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
public class AdmService {
    private AdmInfoRepository admInfoRepository;

    public AdmService(AdmInfoRepository admInfoRepository) {
        this.admInfoRepository = admInfoRepository;
    }

    public AdmInfo findArea(Map<String, Object> paramMap) {
        String lon = CommonUtil.validOneNull(paramMap,"lon");
        String lat = CommonUtil.validOneNull(paramMap,"lat");
        return admInfoRepository.findArea(lon, lat);
    }

}
