package com.danusys.web.commons.api.repository;

import com.danusys.web.commons.api.model.ApiParamConv;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * Project : danusys-webservice-parent
 * Created by Intellij IDEA
 * Developer : kjm
 * Date : 2022/05/25
 * Time : 17:07
 */
public interface ApiConvRepository extends JpaRepository<ApiParamConv,Long> {

    @Query(value = "SELECT value FROM api_param_conv WHERE api_param_seq = :seq and key = :key", nativeQuery = true)
    String findValueByApiParamSeqAndKey(Long seq,String key);
}
