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
public interface BoardMapper {

    String selectBoard = "SELECT * " +
            "FROM t_board" +
            "WHERE 1=1";

    String whereBoard = "<script>" +
            "" +
            "</script>";

    @Select(selectBoard + whereBoard)
    List<Map<String, Object>> selectAll();

    @Select(selectBoard + whereBoard)
    Map<String, Object> selectOne();
    @Select(selectBoard + whereBoard)
    List<HashMap<String, Object>> selectAll(Map<String, Object> param);

    @Select(selectCntBoard + whereBoard)
    Integer selectOne();
}
