package com.danusys.web.platform.mapper;

import com.danusys.web.commons.util.EgovMap;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

/*
* 공지사항 관련 Mapper
* */
@Mapper
public interface NoticeMapper {

    @SelectProvider(type=NoticeSqlProvider.class, method="selectList")
    List<EgovMap> selectList(Map<String, Object> param);

    @SelectProvider(type=NoticeSqlProvider.class, method="selectOne")
    EgovMap selectOne(@Param("noticeSeq") int noticeSeq);

    @InsertProvider(type=NoticeSqlProvider.class, method="insert")
    int insert(Map<String, Object> param);

    @UpdateProvider(type=NoticeSqlProvider.class, method="update")
    int update(Map<String, Object> param);

    @DeleteProvider(type=NoticeSqlProvider.class, method="delete")
    int delete(Map<String, Object> param);
}