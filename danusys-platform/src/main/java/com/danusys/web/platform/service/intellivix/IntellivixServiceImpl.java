package com.danusys.web.platform.service.intellivix;

import com.danusys.web.commons.api.dto.ApiParamDto;
import com.danusys.web.commons.api.model.ApiParam;
import com.danusys.web.commons.api.repository.ApiParamRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Project : danusys-webservice-parent
 * Created by Intellij IDEA
 * Developer : ndw85
 * Date : 2022/04/25
 * Time : 15:12
 */

@Service
public class IntellivixServiceImpl implements IntellivixService{

    private final ApiParamRepository apiParamRepository;

    public IntellivixServiceImpl(ApiParamRepository apiParamRepository) {
        this.apiParamRepository = apiParamRepository;
    }


    @Override
    @Transactional
    public void updateValue(Long id, ApiParamDto.Request request) throws Exception {
        ApiParam apiParam = apiParamRepository.findAllById(id);
        Optional.ofNullable(apiParam).orElseThrow(() -> {
            return new IllegalArgumentException("check your id");
        });
        apiParam.updateValue(request.getValue());
    }

    @Override
    public ApiParamDto.Response findById(Long id) throws Exception {
        ApiParam apiParam = apiParamRepository.findAllById(id);
        Optional.ofNullable(apiParam).orElseThrow(() -> {
            return new IllegalArgumentException("check your id");
        });
        return new ApiParamDto.Response(apiParam);
    }

}
