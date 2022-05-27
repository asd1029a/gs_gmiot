package com.danusys.web.platform.mapper.event;

import com.danusys.web.commons.app.CommonUtil;
import com.danusys.web.commons.app.SqlUtil;
import org.apache.ibatis.jdbc.SQL;

import java.util.ArrayList;
import java.util.Map;

public class EventSqlProvider {
    public String selectListQry(Map<String, Object> paramMap) {
        SQL sql = new SQL() {{
            String keyword = CommonUtil.validOneNull(paramMap, "keyword");
            String start = CommonUtil.validOneNull(paramMap, "start");
            String length = CommonUtil.validOneNull(paramMap, "length");
            String startDt = CommonUtil.validOneNull(paramMap, "startDt");
            String endDt = CommonUtil.validOneNull(paramMap, "endDt");
            ArrayList eventGrade = CommonUtil.valiArrNull(paramMap, "eventGrade");
            ArrayList eventState = CommonUtil.valiArrNull(paramMap, "eventState");
            ArrayList eventKind = CommonUtil.valiArrNull(paramMap, "eventKind");
            //ArrayList facilityDirection = CommonUtil.valiArrNull(paramMap,"facilityDirection");
            //ArrayList facilityProblem = CommonUtil.valiArrNull(paramMap,"facilityProblem");
            boolean geoFlag = Boolean.parseBoolean(CommonUtil.validOneNull(paramMap, "geojson"));

            String colums =
                    "t1.event_seq, v1.code_value as event_kind, v2.code_value as event_grade, v3.code_value as event_proc_stat, t1.event_address, t1.event_start_dt, t1.event_end_dt" +
                    ", t1.event_manager, t1.event_mng_dt, t1.event_mng_content, t1.insert_dt, t1.station_seq, t1.facility_seq, t1.event_message" +
                    ", v1.code_name AS event_kind_name" + //이벤트 종류 한글명
                    ", v2.code_name AS event_grade_name" + //이벤트 등급 한글명
                    ", v3.code_name AS event_proc_stat_name"; //이벤트 처리상태 한글명

            String tables = "t_event t1 " +
                    "INNER JOIN v_event_kind v1 ON t1.event_kind = v1.code_seq " +
                    "INNER JOIN v_event_grade v2 ON t1.event_grade = v2.code_seq " +
                    "INNER JOIN v_event_proc_stat v3 ON t1.event_proc_stat = v3.code_seq ";

            if (geoFlag) { //geojson 호출시
                colums += ", t2.longitude, t2.latitude ";
                tables += "INNER JOIN t_station t2 ON t1.station_seq = t2.station_seq ";
            }

            SELECT(colums);
            FROM(tables);

//            if(facilityDirection != null && !facilityDirection.isEmpty()) {
//                FROM("t_event t1 " +
//                        "LEFT JOIN v_facility_direction t2 on t1.event_kind = t2.code_value "
//                        + "INNER JOIN t_station t3 ON t1.station_seq  = t3.station_seq");
//                WHERE("code_seq in ('" + StringUtils.join(facilityDirection, "', '") + "')");
//            }else if(facilityProblem != null && !facilityProblem.isEmpty()) {
//                FROM("t_event t1 " +
//                        "LEFT JOIN v_facility_problem t2 on t1.event_kind = t2.code_value "
//                        + "INNER JOIN t_station t3 ON t1.station_seq  = t3.station_seq");
//                WHERE("code_seq in ('" +  StringUtils.join(facilityProblem, "', '") + "')");
//            }else{
//                FROM("t_event t1 " +
//                        "LEFT JOIN t_common_code t2 on t1.event_kind = t2.code_value "
//                        + "INNER JOIN t_station t3 ON t1.station_seq  = t3.station_seq");
//            }
            if (!keyword.equals("")) {
                WHERE("v1.code_value LIKE '%" + keyword + "%'");
            }
            if (!startDt.equals("")) {
                WHERE("t1.insert_dt >= to_timestamp('" + startDt + "', 'YYYY-MM-DD HH24:MI:SS')");
            }
            if (!endDt.equals("")) {
                WHERE("t1.insert_dt <= to_timestamp('" + endDt + "', 'YYYY-MM-DD HH24:MI:SS')");
            }
            if (eventKind != null && !eventKind.isEmpty()) {
                WHERE("v1.code_value" + SqlUtil.getWhereInStr(eventKind));
            }
            if (eventGrade != null && !eventGrade.isEmpty()) {
                WHERE("v2.code_value" + SqlUtil.getWhereInStr(eventGrade));
            }
            if (eventState != null && !eventState.isEmpty()) {
                WHERE("v3.code_value" + SqlUtil.getWhereInStr(eventState));
            }
            if (eventKind != null && !eventKind.isEmpty()) {
                WHERE("v1.code_value" + SqlUtil.getWhereInStr(eventKind));
            }

            if (!start.equals("") && !length.equals("")) {
                LIMIT(length);
                OFFSET(start);
            }
        }};
        return sql.toString();
    }

    public String selectCountQry(Map<String, Object> paramMap) {
        SQL sql = new SQL() {{
            String keyword = CommonUtil.validOneNull(paramMap, "keyword");
            String start = CommonUtil.validOneNull(paramMap, "start");
            String length = CommonUtil.validOneNull(paramMap, "length");
            String startDt = CommonUtil.validOneNull(paramMap, "startDt");
            String endDt = CommonUtil.validOneNull(paramMap, "endDt");
            ArrayList eventGrade = CommonUtil.valiArrNull(paramMap, "eventGrade");
            ArrayList eventState = CommonUtil.valiArrNull(paramMap, "eventState");
            ArrayList eventKind = CommonUtil.valiArrNull(paramMap, "eventKind");

            SELECT("COUNT(*) as count");
            String tables = "t_event t1 " +
                    "INNER JOIN v_event_kind v1 ON t1.event_kind = v1.code_seq " +
                    "INNER JOIN v_event_grade v2 ON t1.event_grade = v2.code_seq " +
                    "INNER JOIN v_event_proc_stat v3 ON t1.event_proc_stat = v3.code_seq ";
            FROM(tables);
            if (!keyword.equals("")) {
                WHERE("v1.code_value LIKE '%" + keyword + "%'");
            }
            if (!startDt.equals("")) {
                WHERE("t1.insert_dt >= to_timestamp('" + startDt + "', 'YYYY-MM-DD HH24:MI:SS')");
            }
            if (!endDt.equals("")) {
                WHERE("t1.insert_dt <= to_timestamp('" + endDt + "', 'YYYY-MM-DD HH24:MI:SS')");
            }
            if (eventKind != null && !eventKind.isEmpty()) {
                WHERE("v1.code_value" + SqlUtil.getWhereInStr(eventKind));
            }
            if (eventGrade != null && !eventGrade.isEmpty()) {
                WHERE("v2.code_value" + SqlUtil.getWhereInStr(eventGrade));
            }
            if (eventState != null && !eventState.isEmpty()) {
                WHERE("v3.code_value" + SqlUtil.getWhereInStr(eventState));
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
