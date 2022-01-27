package com.danusys.web.platform.mapper.config;

import com.danusys.web.commons.util.EgovMap;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;
import java.util.Map;

@Mapper
public interface ConfigMapper {
    @SelectProvider(type= ConfigSqlProvider.class, method = "selectAllCode")
    List<EgovMap> selectAllCode(Map<String, Object> paramMap);

    @SelectProvider(type= ConfigSqlProvider.class, method = "selectOneCode")
    EgovMap selectOneCode(int seq);
}
