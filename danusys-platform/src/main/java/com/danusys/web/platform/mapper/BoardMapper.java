package com.danusys.web.platform.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.HashMap;
import java.util.List;

/*
* 공지사항 관련 Mapper
* */
@Mapper
public interface BoardMapper {

    String selectBoard = "SELECT * " +
            "FROM t_board" +
            "WHERE 1=1";

    String selectCntBoard = "SELECT COUNT(*)" +
            "FROM t_board" +
            "WHERE 1=1";

    String whereBoard = "<script>" +
            "" +
            "</script>";

    @Select(selectBoard + whereBoard)
    List<HashMap<String, Object>> selectAll();

    @Select(selectCntBoard + whereBoard)
    Integer selectOne();
}
