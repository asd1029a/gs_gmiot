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
            ArrayList administZone = CommonUtil.valiArrNull(paramMap, "administZone");
            String sigCode = CommonUtil.validOneNull(paramMap, "sigCode");
            //ArrayList facilityDirection = CommonUtil.valiArrNull(paramMap,"facilityDirection");
            //ArrayList facilityProblem = CommonUtil.valiArrNull(paramMap,"facilityProblem");
            boolean geoFlag = Boolean.parseBoolean(CommonUtil.validOneNull(paramMap, "geojson"));

            String columns =
                    "t1.event_seq, v1.code_value as event_kind, v2.code_value as event_grade" +
                    ", v3.code_value as event_proc_stat, v3.code_name as event_proc_stat_name, t1.event_address" +
                    ", t1.event_manager, t1.event_mng_dt, t1.event_mng_content, t1.insert_dt, t1.station_seq" +
                    ", t1.facility_seq, t1.event_message, t1.event_end_manager" +
                    ", CASE WHEN t1.event_start_dt IS NOT NULL" +
                    " THEN to_char(t1.event_start_dt, 'YYYY-MM-DD HH24:MI:SS')" +
                    " ELSE '정보없음' END AS event_start_dt" +
                    ", CASE WHEN t1.event_end_dt IS NOT NULL" +
                    " THEN to_char(t1.event_end_dt, 'YYYY-MM-DD HH24:MI:SS')" +
                    " ELSE '정보없음' END AS event_end_dt" +
                    ", t2.facility_name, t3.station_name" +
                    ", v1.code_name AS event_kind_name" + //이벤트 종류 한글명
                    ", v2.code_name AS event_grade_name" + //이벤트 등급 한글명
                    ", v3.code_name AS event_proc_stat_name" + //이벤트 처리상태 한글명
                    ", v4.code_name AS administ_zone_name" + //동 이름
                    ", v5.code_name as facility_kind";

            String tables = "t_event t1 " +
                    "INNER JOIN t_facility t2 ON t1.facility_seq = t2.facility_seq " +
                    "INNER JOIN t_station t3 ON t1.station_seq = t3.station_seq " +
                    "INNER JOIN v_event_kind v1 ON t1.event_kind = v1.code_seq " +
                    "INNER JOIN v_event_grade v2 ON t1.event_grade = v2.code_seq " +
                    "INNER JOIN v_event_proc_stat v3 ON t1.event_proc_stat = v3.code_seq " +
                    "INNER JOIN v_administ v4 ON t2.administ_zone = v4.code_value " +
                    "INNER JOIN v_facility_kind v5 on t2.facility_kind = v5.code_seq";

            if (geoFlag) { //geojson 호출시
                columns += ", t3.longitude, t3.latitude, t3.administ_zone";
            }

            SELECT(columns);
            FROM(tables);

            if (!keyword.equals("")) {
                WHERE("t3.station_name LIKE '%" + keyword + "%'");
            }
            if (!startDt.equals("")) {
                WHERE("t1.insert_dt >= to_timestamp('" + startDt + "', 'YYYY-MM-DD HH24:MI:SS')");
            }
            if (!endDt.equals("")) {
                WHERE("t1.insert_dt <= to_timestamp('" + endDt + "', 'YYYY-MM-DD HH24:MI:SS')");
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
            if (sigCode != null && !sigCode.isEmpty()){
                WHERE("SUBSTRING(v4.code_value, 0, 6) = '" + sigCode + "'");
            }
            if (administZone != null && !administZone.isEmpty()) {
                WHERE( "v4.code_value" + SqlUtil.getWhereInStr(administZone));
            }

            ORDER_BY("event_start_dt desc");

            if (!start.equals("") && !length.equals("")) {
                LIMIT(length);
                OFFSET(start);
            }
        }};
        return sql.toString();
    }

    public String selectCountQry(Map<String, Object> paramMap) {
        paramMap.remove("start");
        SQL sql = new SQL(){{
            SELECT("COUNT(c1.*) count" +
                    ", COUNT(case when c1.event_proc_stat = '1' then 1 end) AS red" +
                    ", COUNT(case when c1.event_proc_stat = '3' then 1 end) AS yellow" +
                    ", COUNT(case when c1.event_proc_stat = '9' then 1 end) AS green");
            FROM("(" + selectListQry(paramMap) + ") AS c1");
        }};
        return sql.toString();
    }

    public String selectOneQry(int seq) {
        SQL sql = new SQL() {{

            SELECT("t1.event_seq, t1.station_seq, t1.event_kind, t1.event_grade, t1.event_proc_stat, t1.event_address" +
                    ", to_char(t1.event_start_dt, 'YYYY-MM-DD HH24:MI:SS') event_start_dt" +
                    ", to_char(t1.event_end_dt, 'YYYY-MM-DD HH24:MI:SS') event_end_dt" +
                    ", to_char(t1.event_mng_dt, 'YYYY-MM-DD HH24:MI:SS') event_mng_dt" +
                    ", t1.event_manager, t1.event_mng_content, t1.event_end_manager, to_char(t1.insert_dt, 'YYYY-MM-DD HH24:MI:SS') insert_dt" +
                    ", t2.station_name, t2.station_kind, t2.administ_zone, t2.address, t2.station_image" +
                    ", to_char(t2.station_compet_dt, 'YYYY-MM-DD HH24:MI:SS') station_compet_dt, t2.latitude, t2.longitude" +
                    ", v1.code_name AS administ_zone_name");
            FROM("t_event t1" +
                    " LEFT JOIN t_station t2 on t1.station_seq = t2.station_seq " +
                    "INNER JOIN v_administ v1 on t2.administ_zone = v1.code_value ");
            WHERE("event_seq =" + seq);
        }};
        return sql.toString();
    }

    public String updateQry(Map<String, Object> paramMap) {
        String eventSeq = paramMap.get("eventSeq").toString();
        SQL sql = new SQL() {{
            UPDATE("t_event");
            SET(SqlUtil.getMultiSetStr(paramMap));
            WHERE("event_seq = " + eventSeq);
        }};
        return sql.toString();
    }

    public String selectProcStatCodeSeqQry(String codeValue){
        SQL sql = new SQL() {{
            SELECT("code_seq");
            FROM("v_event_proc_stat");
            WHERE("code_value = " + codeValue + "::text");
        }};
        return sql.toString();
    }

}
