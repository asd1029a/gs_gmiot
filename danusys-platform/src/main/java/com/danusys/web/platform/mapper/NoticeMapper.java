package com.danusys.web.platform.mapper;

import com.danusys.web.commons.util.EgovMap;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;

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
    Map<String, Object> selectOne(@Param("noticeSeq") int noticeSeq);
}