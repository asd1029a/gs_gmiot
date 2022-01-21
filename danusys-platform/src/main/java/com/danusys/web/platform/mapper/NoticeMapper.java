package com.danusys.web.platform.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
* 공지사항 관련 Mapper
* */
@Mapper
public interface NoticeMapper {

    String selectNotice = "SELECT * " +
            "FROM t_notice" +
            "WHERE 1=1";

    String whereNotice = "<script>" +
            "" +
            "</script>";

    @Select(selectNotice + whereNotice)
    List<Map<String, Object>> selectAll();

    @Select(selectNotice + whereNotice)
    Map<String, Object> selectOne();

    @Select(selectNotice + whereNotice)
    List<HashMap<String, Object>> selectAll(Map<String, Object> param);

}
