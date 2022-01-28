package com.danusys.web.platform.mapper.event;

import com.danusys.web.commons.util.EgovMap;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;
import java.util.Map;

@Mapper
public interface EventMapper {
    @SelectProvider(type=EventSqlProvider.class, method = "selectAll")
    List<EgovMap> selectAll(Map<String, Object> paramMap);

    @SelectProvider(type=EventSqlProvider.class, method = "selectOne")
    EgovMap selectOne(int seq);
}
