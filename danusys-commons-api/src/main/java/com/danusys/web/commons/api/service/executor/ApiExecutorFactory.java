package com.danusys.web.commons.api.service.executor;

import com.danusys.web.commons.api.types.ApiType;

/**
 * Project : danusys-webservice-parent
 * Created by IntelliJ IDEA
 * Developer : kai
 * Date : 2021/11/02
 * Time : 3:31 오후
 */
public interface ApiExecutorFactory {
    ApiExecutor create(ApiType apiType);
}
