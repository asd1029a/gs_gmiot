package com.danusys.web.platform.mapper.event;

import com.danusys.web.commons.util.CommonUtil;
import org.apache.ibatis.jdbc.SQL;

import java.util.Map;

public class EventSqlProvider {
    public String selectListQry(Map<String, Object> paramMap) {
        SQL sql = new SQL() {{
            String keyword = CommonUtil.validOneNull(paramMap,"keyword");
            String start =  CommonUtil.validOneNull(paramMap,"start");
            String length =  CommonUtil.validOneNull(paramMap,"length");

            SELECT("*, " +
                    "'' as station_seq, '' as station_name, '' as station_kind, " +
                    "'' as dong_short_nm, '' as address, " +
                    "'' as facility_seq, '' as facility_kind");
            FROM("t_event");
            if(!keyword.equals("")) {
                WHERE("event_kind LIKE" + keyword);
            }
            if (!start.equals("") && !length.equals("")) {
                LIMIT(length);
                OFFSET(start);
            }
            //WHERE("seq =" + seq);
        }};
        return sql.toString();
    }
    public String selectOneQry(int seq) {
        SQL sql = new SQL() {{

            SELECT("*");
            FROM("t_event");
            WHERE("event_seq =" + seq);
        }};
        return sql.toString();
    }
}
