package com.danusys.guardian.service.api;

import com.danusys.guardian.model.Api;
import com.danusys.guardian.service.executor.ApiExecutor;
import com.danusys.guardian.service.executor.ApiExecutorFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * Project : danusys-guardian-parent
 * Created by IntelliJ IDEA
 * Developer : kai
 * Date : 2021/11/03
 * Time : 2:31 오후
 */
@Slf4j
@Service
public class ApiService {

    @Autowired
    private ApiExecutorFactory apiExecutorFactory;

//    public ApiService(ApiExecutorFactory apiExecutorFactory) {
//        this.apiExecutorFactory = apiExecutorFactory;
//    }


//    @Async(AsyncConfig.EXECUTOR_DANU_BEAN_NAME)
//    public CompletableFuture<JsonResponse> executeTest(final Api api) throws Exception {
//
//        return execute(beforeExecute(api));
//    }


//    public JsonResponse executeSyncTest(final Api api) throws Exception {
//        return executeSync(beforeExecute(api));
//    }

//    @Async(AsyncConfig.EXECUTOR_DANU_BEAN_NAME)
//    public CompletableFuture<JsonResponse> execute(Api api) throws Exception {
//        ApiExecutor apiExecutor = apiExecutorFactory.create(api.getApiType());
//        return apiExecutor.execute(api);
//    }

    private Api beforeExecute(final Api api) {
//        Set<ApiParam> params = api.getApiRequestParams();
//        for (ApiParam param : params) {
//            if (StringUtils.isEmpty(param.getValue()) && param.isRequired())
//                throw new IllegalArgumentException(String.format("요청 파라미터(%s) 의 테스트 값(%s) 올바르지 않습니다", param.getFieldNm(), param.getValue()));

//            if (param.getDataType() == DataType.NUMBER)
//                param.getValue().set(NumberUtils.toNumber(param.getValue()));
//            else
//                param.getValue().set(param.getTestValue());
//        }
        return api;
    }

    public ResponseEntity<?> execute(Api api) throws Exception {
        ApiExecutor apiExecutor = apiExecutorFactory.create(api.getApiType());
        log.trace("### apiExecutor : {} 방식 호출", api.getApiType().toString());

        return apiExecutor.execute(api);
    }
}
