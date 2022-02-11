package com.danusys.web.platform.mapper.station;

import com.danusys.web.commons.util.CommonUtil;
import com.danusys.web.platform.util.SqlUtil;
import org.apache.ibatis.jdbc.SQL;

import java.util.Map;

public class StationSqlProvider {

    public String selectListQry(Map<String, Object> paramMap) {

        String keyword = CommonUtil.validOneNull(paramMap,"keyword");
        String start =  CommonUtil.validOneNull(paramMap,"start");
        String length =  CommonUtil.validOneNull(paramMap,"length");

        SQL sql = new SQL() {{
            SELECT("*");
            FROM("t_station ts");
            LEFT_OUTER_JOIN("t_facility tf ON ts.station_seq = tf.station_seq");
            if(!keyword.equals("")) {
                WHERE("ts.station_name LIKE" + keyword);
            }
            if (!start.equals("") && !length.equals("")) {
                LIMIT(length);
                OFFSET(start);
            }
        }};
        return sql.toString();
    }

    public String selectCountQry(Map<String, Object> paramMap) {

        String keyword = CommonUtil.validOneNull(paramMap,"keyword");

        SQL sql = new SQL() {{
            SELECT("COUNT(*) AS count");
            FROM("t_station ts");
            LEFT_OUTER_JOIN("t_facility tf ON ts.station_seq = tf.station_seq");
            if(!keyword.equals("")) {
                WHERE("ts.station_name LIKE" + keyword);
            }
        }};
        return sql.toString();
    }

    public String selectOneQry(int stationSeq) {
        SQL sql = new SQL() {{
            SELECT("*");
            FROM("t_station");
            WHERE("station_seq=" + stationSeq);
        }};
        return sql.toString();
    }

    public String insertQry(Map<String, Object> paramMap) {
        Map<String, Object> qryMap = SqlUtil.getInsertValuesStr(paramMap);

        SQL sql = new SQL() {{
        }};
        return sql.toString();
    }

    public String updateQry(Map<String, Object> paramMap) {
        String noticeSeq = paramMap.get("noticeSeq").toString();

        SQL sql = new SQL() {{
        }};
        return sql.toString();
    }

    public String deleteQry(int seq) {
        SQL sql = new SQL() {{
        }};
        return sql.toString();
    }
}
