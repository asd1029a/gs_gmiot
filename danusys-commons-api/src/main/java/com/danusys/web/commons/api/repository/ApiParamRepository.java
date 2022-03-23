package com.danusys.web.commons.api.repository;

import com.danusys.web.commons.api.model.ApiParam;
import com.danusys.web.commons.api.types.ParamType;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Project : danusys-webservice-parent
 * Created by IntelliJ IDEA
 * Developer : kai
 * Date : 2021/11/24
 * Time : 6:14 오후
 */
public interface ApiParamRepository extends PagingAndSortingRepository<ApiParam, Long> {
    List<ApiParam> findAllByApiIdAndParamType(Long apiId, ParamType paramType);

    List<ApiParam> findAllByParentSeq(int seq);
}
