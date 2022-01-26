package com.danusys.web.platform.mapper;

import com.danusys.web.commons.util.EgovMap;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;
import java.util.Map;

@Mapper
public interface CommonMapper {
    @SelectProvider(type=CommonSqlProvider.class, method = "selectCodeList")
    List<EgovMap> selectCodeAll(Map<String, Object> paramMap);

    @SelectProvider(type=CommonSqlProvider.class, method = "selectCodeOne")
    EgovMap selectCodeOne(@Param("codeSeq") int codeSeq);
}
