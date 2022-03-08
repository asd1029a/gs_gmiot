package com.danusys.web.platform.mapper.common;

import com.danusys.web.commons.app.EgovMap;
import org.apache.ibatis.annotations.*;

import java.util.List;

/*
 * 공통 Mapper
 */

@Mapper
public interface CommonMapper {

    @SelectProvider(type=sqlProviderChain.class, method = "selectListQuery")
    List<EgovMap> selectList(@Param("qry") String qry);

    @SelectProvider(type=sqlProviderChain.class, method="selectOneQuery")
    EgovMap selectOne(@Param("qry") String qry);

    @SelectProvider(type=sqlProviderChain.class, method="selectKeyQuery")
    int selectKey(@Param("qry") String qry);

    @InsertProvider(type=sqlProviderChain.class, method="insertQuery")
    int insert(@Param("qry") String qry);

    @UpdateProvider(type=sqlProviderChain.class, method="updateQuery")
    int update(@Param("qry") String qry);

    @DeleteProvider(type=sqlProviderChain.class, method="deleteQuery")
    int delete(@Param("qry") String qry);

}
