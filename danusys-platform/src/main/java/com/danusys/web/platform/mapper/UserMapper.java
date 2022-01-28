package com.danusys.web.platform.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.HashMap;
import java.util.List;


/**
 * 사용자 관련 Mapper
 */
@Mapper
public interface UserMapper {
    String selectUser = "SELECT user_seq, user_id, email, tel" +
            ", address, detail_address, last_login_dt, insert_user_seq" +
            ", update_user_seq, insert_dt, update_dt, status" +
            " FROM t_user";

    @Select(selectUser)
    List<HashMap<String, Object>> findAll();

    @Select(selectUser + " WHERE USER_SEQ = #{user_seq}")
    HashMap<String, Object> findOne();
}