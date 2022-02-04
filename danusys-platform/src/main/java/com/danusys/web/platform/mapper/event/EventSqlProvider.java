package com.danusys.web.platform.mapper.event;

import org.apache.ibatis.jdbc.SQL;

import java.util.Map;

public class EventSqlProvider {
    public String selectAllEventQry(Map<String, Object> paramMap) {
        SQL sql = new SQL() {{

            SELECT("*, " +
                    "'' as station_seq, '' as station_name, '' as station_kind, " +
                    "'' as dong_short_nm, '' as address, " +
                    "'' as facility_seq, '' as facility_kind");
            FROM("t_event");
            //WHERE("seq =" + seq);
        }};
        return sql.toString();
    }
    public String selectOneEventQry(int seq) {
        SQL sql = new SQL() {{

            SELECT("*");
            FROM("t_event");
            WHERE("event_seq =" + seq);
        }};
        return sql.toString();
    }
}
