package com.danusys.web.platform.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/*
* 공지사항 관련 Mapper
* */
@Mapper
public interface NoticeMapper {

    String selectNotice = "SELECT * " +
            "FROM t_notice " +
            "WHERE 1=1";

    String whereNotice = "<script>" +
            "<if test= start != null and length != null>" +
            " LIMIT #{start} , #{length}" +
            "</if>" +
            "</script>";

    @Select(selectNotice + whereNotice)
    Map<String, Object> selectOne();

    @Select(selectNotice)
    List<Map<String, Object>> selectAll(Map<String, Object> param);

}
