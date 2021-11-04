package com.danusys.guardian.service.executor;

import com.danusys.guardian.type.ApiType;

/**
 * Project : danusys-guardian-parent
 * Created by IntelliJ IDEA
 * Developer : kai
 * Date : 2021/11/02
 * Time : 3:31 오후
 */
public interface ApiExecutorFactory {
    ApiExecutor create(ApiType apiType);
}
