package com.danusys.web.platform.service.intellivix;

import com.danusys.web.commons.api.dto.ApiParamDto;
import org.springframework.http.ResponseEntity;


/**
 * Project : danusys-webservice-parent
 * Created by Intellij IDEA
 * Developer : ndw85
 * Date : 2022/04/25
 * Time : 15:07
 */
public interface IntellivixService {
    void updateValue(Long id, ApiParamDto.Request param) throws Exception;
    ApiParamDto.Response findById(Long id) throws Exception;
}
