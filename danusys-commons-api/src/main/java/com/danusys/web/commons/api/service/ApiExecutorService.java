package com.danusys.web.commons.api.service;

import com.danusys.web.commons.api.model.Api;
import com.danusys.web.commons.api.model.ApiParam;
import com.danusys.web.commons.api.repository.ApiParamRepository;
import com.danusys.web.commons.api.repository.ApiRepository;
import com.danusys.web.commons.api.types.ParamType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Project : danusys-webservice-parent
 * Created by IntelliJ IDEA
 * Developer : kai
 * Date : 2021/11/03
 * Time : 2:31 오후
 */
@Slf4j
@Service
//@NoArgsConstructor
public class ApiExecutorService {

    private ApiRepository apiRepository;
    private ApiParamRepository apiParamRepository;

    public ApiExecutorService(ApiRepository apiRepository, ApiParamRepository apiParamRepository) {
        this.apiRepository = apiRepository;
        this.apiParamRepository = apiParamRepository;
    }

    /**
     * callUrl 조건으로 조회해서 Api 객체를 리턴
     * @param callUrl
     * @return
     */
    public Api findByCallUrl(String callUrl) {
        log.trace("callUrl : {}", callUrl);

        return apiRepository.findByCallUrl(callUrl);
    }

    public List<ApiParam> findApiParam(Long apiId, ParamType paramType) {
        return apiParamRepository.findAllByApiIdAndParamType(apiId, paramType);
    }

}
