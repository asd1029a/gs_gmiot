package com.danusys.web.commons.api.service;

import com.danusys.web.commons.api.model.ApiParam;
import com.danusys.web.commons.api.repository.ApiConvRepository;
import com.danusys.web.commons.api.repository.ApiParamRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ApiConvService {

    private ApiConvRepository apiConvRepository;
    private ApiParamRepository apiParamRepository;

    public ApiConvService(ApiConvRepository apiConvRepository,
                          ApiParamRepository apiParamRepository){
        this.apiConvRepository = apiConvRepository;
        this.apiParamRepository = apiParamRepository;
    }

    public String eventConverter(ApiParam apiParam,String key){
        Long id = apiParam.getId();
        return apiConvRepository.findValueByApiParamSeqAndKey(id,key);
    }
}
