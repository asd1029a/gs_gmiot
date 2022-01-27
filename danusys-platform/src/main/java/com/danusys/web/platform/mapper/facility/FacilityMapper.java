package com.danusys.web.platform.mapper.facility;

import com.danusys.web.commons.util.EgovMap;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

/*
* 시설물 관련 Mapper
*/
@Mapper
public interface FacilityMapper {

    @SelectProvider(type=FacilitySqlProvider.class, method="selectList")
    List<EgovMap> selectList(Map<String, Object> param);

    @SelectProvider(type=FacilitySqlProvider.class, method="selectOne")
    EgovMap selectOne(int seq);

    @InsertProvider(type=FacilitySqlProvider.class, method="insert")
    int insert(Map<String, Object> param);

    @UpdateProvider(type=FacilitySqlProvider.class, method="update")
    int update(Map<String, Object> param);

    @DeleteProvider(type=FacilitySqlProvider.class, method="delete")
    int delete(Map<String, Object> param);
}
