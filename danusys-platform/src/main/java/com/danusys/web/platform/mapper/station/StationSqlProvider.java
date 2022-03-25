package com.danusys.web.platform.mapper.station;

import com.danusys.web.commons.app.CommonUtil;
import com.danusys.web.commons.app.SqlUtil;
import org.apache.ibatis.jdbc.SQL;

import java.util.Map;

public class StationSqlProvider {

    public String selectListQry(Map<String, Object> paramMap) {
        String keyword = CommonUtil.validOneNull(paramMap,"keyword");
        String start =  CommonUtil.validOneNull(paramMap,"start");
        String length =  CommonUtil.validOneNull(paramMap,"length");

//        SQL innerSql = new SQL() {{
//            SELECT("tf.station_seq, count(*) as facility_cnt");
//            FROM("t_facility tf");
//            GROUP_BY("tf.station_seq");
//        }};

        SQL sql = new SQL() {{
            SELECT("ts.*, count(*) over(partition by latitude, longitude) as node_cnt");
            FROM("t_station ts");
            if(!keyword.equals("")) {
                WHERE("ts.station_name LIKE '%" + keyword +"%'");
            }
            if (!start.equals("") && !length.equals("")) {
                LIMIT(start);
                OFFSET(length);
            }
            //INNER_JOIN("(" + innerSql.toString() + ") t1 on ts.station_seq = t1.station_seq");
        }};
        System.out.println(sql.toString());
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
