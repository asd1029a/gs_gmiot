package com.danusys.web.commons.api.repository;

import com.danusys.web.commons.api.model.Api;
import org.springframework.data.repository.CrudRepository;

/**
 * Project : danusys-webservice-parent
 * Created by IntelliJ IDEA
 * Developer : kai
 * Date : 2021/11/24
 * Time : 6:14 오후
 */
public interface ApiRepository extends CrudRepository<Api, Long> {
    Api findByCallUrl(String callUrl);
}
