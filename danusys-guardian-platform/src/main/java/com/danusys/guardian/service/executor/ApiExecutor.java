package com.danusys.guardian.service.executor;

import com.danusys.guardian.model.Api;
import org.springframework.http.ResponseEntity;

/**
 * Project : danusys-guardian-parent
 * Created by IntelliJ IDEA
 * Developer : kai
 * Date : 2021/11/02
 * Time : 3:31 오후
 */
public interface ApiExecutor {
    ResponseEntity execute(Api api) throws Exception;
}
