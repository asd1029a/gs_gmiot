package com.danusys.web.platform.mapper.station;

import com.danusys.web.commons.util.EgovMap;
import com.danusys.web.platform.mapper.station.StationSqlProvider;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface StationMapper {

    @SelectProvider(type= StationSqlProvider.class, method="selectList")
    List<EgovMap> selectList(Map<String, Object> param);

    @SelectProvider(type=StationSqlProvider.class, method="selectOne")
    EgovMap selectOne(int seq);

    @InsertProvider(type=StationSqlProvider.class, method="insert")
    int insert(Map<String, Object> param);

    @UpdateProvider(type=StationSqlProvider.class, method="update")
    int update(Map<String, Object> param);

    @DeleteProvider(type=StationSqlProvider.class, method="delete")
    int delete(Map<String, Object> param);
}
