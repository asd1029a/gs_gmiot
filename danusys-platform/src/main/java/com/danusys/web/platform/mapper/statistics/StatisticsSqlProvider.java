package com.danusys.web.platform.mapper.statistics;

import com.danusys.web.commons.app.CommonUtil;
import com.danusys.web.commons.app.SqlUtil;
import org.apache.ibatis.jdbc.SQL;
import org.springframework.beans.factory.annotation.Value;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class StatisticsSqlProvider {

    public String selectSumQry(Map<String, Object> paramMap) {
        Map<String, String> timePatternMap = new HashMap<>();
        timePatternMap.put("hour", "YYYY/MM/DD HH24");
        timePatternMap.put("day", "YYYY/MM/DD");
        timePatternMap.put("week", "YYYY/MM-W");
        timePatternMap.put("month", "YYYY/MM");

        SQL sql = new SQL() {{
            String startDt = CommonUtil.validOneNull(paramMap, "startDt");
            String endDt = CommonUtil.validOneNull(paramMap, "endDt");
            String unit = CommonUtil.validOneNull(paramMap, "unit");
            String timePattern = timePatternMap.get(unit);
            ArrayList<String> eventKind = CommonUtil.valiArrNull(paramMap, "eventKind");
            ArrayList<String> administZone = CommonUtil.valiArrNull(paramMap, "administZone");

            SQL subQry2 = new SQL() {{
                SQL subQry1 = new SQL() {{
                    SELECT("to_char(t1.insert_dt, '" + timePattern + "') AS x_axis," +
                            "   count(case when v2.code_value = '10' then 1 end) urgent," +
                            "   count(case when v2.code_value = '20' then 1 end) caution");
                    FROM("t_event t1" +
                            "   INNER JOIN t_facility t2 ON t1.facility_seq = t2.facility_seq" +
                            "   INNER JOIN v_event_kind v1 ON t1.event_kind = v1.code_seq" +
                            "   INNER JOIN v_event_grade v2 ON t1.event_grade = v2.code_seq" +
//                          현재 뷰테이블과 시설물 구역이 맞지 않아 임시로 조회
//                          "   INNER JOIN v_administ v3 on t2.administ_zone = v3.code_value");
                            "   INNER JOIN t_area_emd v3 ON t2.administ_zone = v3.emd_cd");
                    if (!endDt.equals("")) {
                        WHERE("t1.insert_dt <= to_timestamp('" + endDt + "', 'YYYY-MM-DD HH24:MI:SS')");
                    }
                    if (eventKind != null && !eventKind.isEmpty()) {
                        WHERE("v1.code_value" + SqlUtil.getWhereInStr(eventKind));
                    }

//                    현재는 데이터가 안맞아서 주석 해놓음
//                    if (administZone != null && !administZone.isEmpty()) {
////                        WHERE("v3.code_value" + SqlUtil.getWhereInStr(administZone));
//                        WHERE("v3.emd_cd" + SqlUtil.getWhereInStr(administZone));
//                    }

                    GROUP_BY("to_char(t1.insert_dt, '" + timePattern + "')");
                }};

                SELECT("x_axis, urgent, caution," +
                        "SUM(urgent) OVER (ORDER BY x_axis) acc_urgent," +
                        "SUM(caution) OVER (ORDER BY x_axis) acc_caution");
                FROM("(" + subQry1.toString() + ") t");
            }};

            SELECT("t.*");
            FROM("(" + subQry2.toString() + ")t");

            if (!startDt.equals("")) {
                String startTimePattern = timePattern;
                if (unit.equals("week")) {
                    startTimePattern = "YYYY/MM/DD";
                }
                WHERE("to_timestamp(x_axis, '" + timePattern + "') >= to_timestamp('" + startDt + "', '" + startTimePattern + "')");
            }
            ORDER_BY("x_axis");
        }};
        return sql.toString();
    }

    public String selectAvgQry(Map<String, Object> paramMap) {
        Map<String, String> timePatternMap = new HashMap<>();
        timePatternMap.put("hour", "YYYY/MM/DD HH24");
        timePatternMap.put("day", "YYYY/MM/DD");
        timePatternMap.put("week", "YYYY/MM-W");
        timePatternMap.put("month", "YYYY/MM");

        Map<String, String> avgTimePatternMap = new HashMap<>();
        avgTimePatternMap.put("hour", "HH24");
        avgTimePatternMap.put("day", "DD");
        avgTimePatternMap.put("week", "W");
        avgTimePatternMap.put("month", "MM");

        SQL sql = new SQL() {{
            String startDt = CommonUtil.validOneNull(paramMap, "startDt");
            String endDt = CommonUtil.validOneNull(paramMap, "endDt");
            String unit = CommonUtil.validOneNull(paramMap, "unit");
            String timePattern = timePatternMap.get(unit);
            String avgTimePattern = avgTimePatternMap.get(unit);
            ArrayList<String> eventKind = CommonUtil.valiArrNull(paramMap, "eventKind");
            ArrayList<String> administZone = CommonUtil.valiArrNull(paramMap, "administZone");

            SQL subQry1 = new SQL() {{
                SELECT("to_char(t1.insert_dt, '" + timePattern + "') AS x_axis," +
                        "   count(case when v2.code_value = '10' then 1 end) urgent," +
                        "   count(case when v2.code_value = '20' then 1 end) caution");
                FROM("t_event t1" +
                        "   INNER JOIN t_facility t2 ON t1.facility_seq = t2.facility_seq" +
                        "   INNER JOIN v_event_kind v1 ON t1.event_kind = v1.code_seq" +
                        "   INNER JOIN v_event_grade v2 ON t1.event_grade = v2.code_seq" +
//                          현재 뷰테이블과 시설물 구역이 맞지 않아 임시로 조회
//                          "   INNER JOIN v_administ v3 on t2.administ_zone = v3.code_value");
                        "   INNER JOIN t_area_emd v3 ON t2.administ_zone = v3.emd_cd");
                if (!startDt.equals("")) {
                    WHERE("t1.insert_dt >= to_timestamp('" + startDt + "', 'YYYY-MM-DD HH24:MI:SS')");
                }
                if (!endDt.equals("")) {
                    WHERE("t1.insert_dt <= to_timestamp('" + endDt + "', 'YYYY-MM-DD HH24:MI:SS')");
                }
                if (eventKind != null && !eventKind.isEmpty()) {
                    WHERE("v1.code_value" + SqlUtil.getWhereInStr(eventKind));
                }

//                    현재는 데이터가 안맞아서 주석 해놓음
//                    if (administZone != null && !administZone.isEmpty()) {
////                        WHERE("v3.code_value" + SqlUtil.getWhereInStr(administZone));
//                        WHERE("v3.emd_cd" + SqlUtil.getWhereInStr(administZone));
//                    }

                GROUP_BY("to_char(t1.insert_dt, '" + timePattern + "')");
            }};

            SELECT("to_char(to_timestamp(x_axis, '" + timePattern + "'), '" + avgTimePattern + "') AS x_axis" +
                    "     , min(urgent)  AS min_urgent" +
                    "     , max(urgent)  AS max_urgent" +
                    "     , min(caution) AS min_caution" +
                    "     , max(caution) AS max_caution");
            FROM("(" + subQry1.toString() + ")t");
            GROUP_BY("to_char(to_timestamp(x_axis, '" + timePattern + "'), '" + avgTimePattern + "')");

        }};
        return sql.toString();
    }

    public String selectMapQry(Map<String, Object> paramMap) {
        SQL sql = new SQL() {{
            String startDt = CommonUtil.validOneNull(paramMap, "startDt");
            String endDt = CommonUtil.validOneNull(paramMap, "endDt");
            ArrayList<String> eventKind = CommonUtil.valiArrNull(paramMap, "eventKind");

            SELECT("v3.emd_nm AS name, count(*) AS value");
            FROM("t_event t1" +
                    "   INNER JOIN t_facility t2 ON t1.facility_seq = t2.facility_seq" +
                    "   INNER JOIN v_event_kind v1 ON t1.event_kind = v1.code_seq" +
                    "   INNER JOIN v_event_grade v2 ON t1.event_grade = v2.code_seq" +
                    "   INNER JOIN t_area_emd v3 ON t2.administ_zone = v3.emd_cd");

            if (!startDt.equals("")) {
                WHERE("t1.insert_dt >= to_timestamp('" + startDt + "', 'YYYY-MM-DD HH24:MI:SS')");
            }
            if (!endDt.equals("")) {
                WHERE("t1.insert_dt <= to_timestamp('" + endDt + "', 'YYYY-MM-DD HH24:MI:SS')");
            }
            if (eventKind != null && !eventKind.isEmpty()) {
                WHERE("v1.code_value" + SqlUtil.getWhereInStr(eventKind));
            }
            GROUP_BY("name");
        }};
        return sql.toString();
    }

    public String selectGeoJsonQry(String emdCode) {
        SQL sql = new SQL() {{
            SELECT("col_adm_se, emd_cd, emd_nm AS name, ST_ASGeoJSON(geom) AS coordinates");
            FROM("t_area_emd");
            WHERE("col_adm_se IN ('" + emdCode + "')");
        }};
        return sql.toString();
    }

}
