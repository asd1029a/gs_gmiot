package com.danusys.web.platform.service.api;

import com.danusys.web.platform.model.Api;
import com.danusys.web.platform.service.executor.ApiExecutor;
import com.danusys.web.platform.service.executor.ApiExecutorFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * Project : danusys-webservice-parent
 * Created by IntelliJ IDEA
 * Developer : kai
 * Date : 2021/11/03
 * Time : 2:31 오후
 */
@Slf4j
@Service
public class ApiService {


    private ApiExecutorFactory apiExecutorFactory;

    public ApiService(ApiExecutorFactory apiExecutorFactory) {
        this.apiExecutorFactory = apiExecutorFactory;
    }

    public ResponseEntity<?> execute(Api api) throws Exception {
        ApiExecutor apiExecutor = apiExecutorFactory.create(api.getApiType());
        log.trace("### apiExecutor : {} 방식 호출", api.getApiType().toString());

        return apiExecutor.execute(api);
    }
}
