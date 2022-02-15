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

    public String selectCountQry(Map<String, Object> paramMap) {
        SQL sql = new SQL() {{
            String keyword = paramMap.get("keyword").toString();

            SELECT("COUNT(*) as count");
            FROM("t_event");
            if(keyword != null && !keyword.equals("")) {
                WHERE("event_kind LIKE" + keyword);
            }
        }};
        return sql.toString();
    }

    public String selectOneQry(int seq) {
        SQL sql = new SQL() {{

            SELECT("t1.event_seq, t1.station_seq, t1.event_kind, t1.event_grade, t1.event_proc_stat, t1.event_address" +
                    ", to_char(t1.event_start_dt, 'YYYY-MM-DD HH24:MI:SS') event_start_dt" +
                    ", to_char(t1.event_end_dt, 'YYYY-MM-DD HH24:MI:SS') event_end_dt" +
                    ", to_char(t1.event_mng_dt, 'YYYY-MM-DD HH24:MI:SS') event_mng_dt" +
                    ", t1.event_manager, t1.event_mng_content, to_char(t1.insert_dt, 'YYYY-MM-DD HH24:MI:SS') insert_dt" +
                    ", t2.station_name, t2.station_kind, t2.administ_zone, t2.address, t2.station_image" +
                    ", to_char(t2.station_compet_dt, 'YYYY-MM-DD HH24:MI:SS') station_compet_dt, t2.latitude, t2.longitude");
            FROM("t_event t1" +
                    " LEFT JOIN t_station t2 on t1.station_seq = t2.station_seq");
            WHERE("event_seq =" + seq);
        }};
        return sql.toString();
    }
}
