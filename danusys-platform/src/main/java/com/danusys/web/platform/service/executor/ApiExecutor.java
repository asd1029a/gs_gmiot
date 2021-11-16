package com.danusys.web.platform.service.executor;

import com.danusys.web.platform.model.Api;
import org.springframework.http.ResponseEntity;

/**
 * Project : danusys-webservice-parent
 * Created by IntelliJ IDEA
 * Developer : kai
 * Date : 2021/11/02
 * Time : 3:31 오후
 */
public interface ApiExecutor {
    ResponseEntity execute(Api api) throws Exception;
}