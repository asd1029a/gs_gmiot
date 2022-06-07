package com.danusys.web.commons.api.service;

import com.danusys.web.commons.api.model.ApiParam;
import com.danusys.web.commons.api.repository.ApiConvRepository;
import com.danusys.web.commons.api.repository.ApiParamRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApiConvService {

    private final ApiConvRepository apiConvRepository;

    public String eventConverter(ApiParam apiParam,String key){
        Long id = apiParam.getId();
        return apiConvRepository.findValueByApiParamSeqAndKey(id,key);
    }
}
