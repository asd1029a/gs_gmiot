package com.danusys.web.platform.mapper.statistics;

import com.danusys.web.commons.app.CommonUtil;
import com.danusys.web.commons.app.SqlUtil;
import org.apache.ibatis.jdbc.SQL;

import java.security.acl.Group;
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

    String startDt = CommonUtil.validOneNull(paramMap, "startDt");
    String endDt = CommonUtil.validOneNull(paramMap, "endDt");
    String unit = CommonUtil.validOneNull(paramMap, "unit");
    String timePattern = timePatternMap.get(unit);
    ArrayList<String> eventKind = CommonUtil.valiArrNull(paramMap, "eventKind");
    ArrayList<String> administZone = CommonUtil.valiArrNull(paramMap, "administZone");

    SQL sql = new SQL() {{
        SQL subQry2 = new SQL() {{
            SQL subQry1 = new SQL() {{
                SELECT("to_char(t1.insert_dt, '" + timePattern + "') AS x_axis," +
                        "   count(case when v2.code_value = '10' then 1 end) urgent," +
                        "   count(case when v2.code_value = '20' then 1 end) caution");
                FROM("t_event t1" +
                        "    INNER JOIN t_facility t2 ON t1.facility_seq = t2.facility_seq" +
                        "    INNER JOIN t_station t3 ON t1.station_seq = t3.station_seq" +
                        "    INNER JOIN v_event_kind v1 ON t1.event_kind = v1.code_seq" +
                        "    INNER JOIN v_event_grade v2 ON t1.event_grade = v2.code_seq" +
                        "    INNER JOIN v_event_proc_stat v3 ON t1.event_proc_stat = v3.code_seq" +
                        "    INNER JOIN v_administ v4 ON t2.administ_zone = v4.code_value");
                if (!endDt.equals("")) {
                    WHERE("t1.insert_dt <= to_timestamp('" + endDt + "', 'YYYY-MM-DD HH24:MI:SS')");
                }
                if (eventKind != null && !eventKind.isEmpty()) {
                    WHERE("v1.code_value" + SqlUtil.getWhereInStr(eventKind));
                }

                if (administZone != null && !administZone.isEmpty()) {
                    WHERE("v4.code_value" + SqlUtil.getWhereInStr(administZone));
                }

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

    SQL dateSql = new SQL() {{
        SELECT("date_t.t_date AS x_axis, t.caution, t.urgent, t.acc_caution, t.acc_urgent");
        // 시간대 생성
        String startDtT = "";
        String endDtT = "";
        if (startDt.equals("")) {
            startDtT = "now()::date - interval '30 " + unit + "'";
        } else {
            startDtT = "'" + startDt + "'";
        }

        if (endDt.equals("")) {
            endDtT = "now()::date";
        } else {
            endDtT = "'" + endDt + "'";
        }
        FROM("(select(to_char(generate_series(" + startDtT + ", " + endDtT + ", '1 " + unit + "'::interval), '" + timePattern + "'))::text as t_date) date_t");
        LEFT_OUTER_JOIN("(" + sql.toString() + ") t on date_t.t_date = t.x_axis");
        ORDER_BY("x_axis");
    }};

    return dateSql.toString();
}

    public String selectAvgQry(Map<String, Object> paramMap) {
        Map<String, String> timePatternMap = new HashMap<>();
        timePatternMap.put("hour", "YYYY/MM/DD HH24");
        timePatternMap.put("day", "YYYY/MM/DD D");
        timePatternMap.put("week", "YYYY/MM-W");
        timePatternMap.put("month", "YYYY/MM");

        Map<String, String> avgTimePatternMap = new HashMap<>();
        avgTimePatternMap.put("hour", "HH24");
        avgTimePatternMap.put("day", "D");
        avgTimePatternMap.put("week", "W");
        avgTimePatternMap.put("month", "MM");

        String startDt = CommonUtil.validOneNull(paramMap, "startDt");
        String endDt = CommonUtil.validOneNull(paramMap, "endDt");
        String unit = CommonUtil.validOneNull(paramMap, "unit");
        String timePattern = timePatternMap.get(unit);
        String avgTimePattern = avgTimePatternMap.get(unit);
        ArrayList<String> eventKind = CommonUtil.valiArrNull(paramMap, "eventKind");
        ArrayList<String> administZone = CommonUtil.valiArrNull(paramMap, "administZone");

        SQL sql = new SQL() {{
            SQL subQry1 = new SQL() {{
                SELECT("to_char(t1.insert_dt, '" + timePattern + "') AS x_axis," +
                        "   count(case when v2.code_value = '10' then 1 end) urgent," +
                        "   count(case when v2.code_value = '20' then 1 end) caution");
                FROM("t_event t1" +
                        "    INNER JOIN t_facility t2 ON t1.facility_seq = t2.facility_seq" +
                        "    INNER JOIN t_station t3 ON t1.station_seq = t3.station_seq" +
                        "    INNER JOIN v_event_kind v1 ON t1.event_kind = v1.code_seq" +
                        "    INNER JOIN v_event_grade v2 ON t1.event_grade = v2.code_seq" +
                        "    INNER JOIN v_event_proc_stat v3 ON t1.event_proc_stat = v3.code_seq" +
                        "    INNER JOIN v_administ v4 ON t2.administ_zone = v4.code_value");
                if (!startDt.equals("")) {
                    WHERE("t1.insert_dt >= to_timestamp('" + startDt + "', 'YYYY-MM-DD HH24:MI:SS')");
                }
                if (!endDt.equals("")) {
                    WHERE("t1.insert_dt <= to_timestamp('" + endDt + "', 'YYYY-MM-DD HH24:MI:SS')");
                }
                if (eventKind != null && !eventKind.isEmpty()) {
                    WHERE("v1.code_value" + SqlUtil.getWhereInStr(eventKind));
                }

                if (administZone != null && !administZone.isEmpty()) {
                    WHERE("v4.code_value" + SqlUtil.getWhereInStr(administZone));
                }

                GROUP_BY("to_char(t1.insert_dt, '" + timePattern + "')");
            }};

            SELECT("to_char(to_timestamp(x_axis, '" + timePattern + "'), '" + avgTimePattern + "') AS x_axis" +
                    "     , min(urgent)  AS min_urgent" +
                    "     , max(urgent)  AS max_urgent" +
                    "     , min(caution) AS min_caution" +
                    "     , max(caution) AS max_caution" +
                    "     , avg(urgent)  AS avg_urgent" +
                    "     , avg(caution) AS avg_caution");
            FROM("(" + subQry1.toString() + ")t");
            GROUP_BY("to_char(to_timestamp(x_axis, '" + timePattern + "'), '" + avgTimePattern + "')");
        }};

        SQL dateSql = new SQL() {{
            SELECT("date_t.t_date AS x_axis, t.min_urgent, t.max_urgent, t.min_caution, t.max_caution, t.avg_urgent, t.avg_caution");
            // 시간대 생성
            String startDtT = "";
            String endDtT = "";
            if (startDt.equals("")) {
                startDtT = "now()::date - interval '30 " + unit + "'";
            } else {
                startDtT = "'" + startDt + "'";
            }

            if (endDt.equals("")) {
                endDtT = "now()::date";
            } else {
                endDtT = "'" + endDt + "'";
            }
            FROM("(select(to_char(generate_series(" + startDtT + ", " + endDtT + ", '1 " + unit + "'::interval), '" + avgTimePattern + "'))::text as t_date group by t_date) date_t");
            LEFT_OUTER_JOIN("(" + sql.toString() + ") t on date_t.t_date = t.x_axis");
            ORDER_BY("x_axis");
        }};

        return dateSql.toString();
    }

    public String selectMapQry(Map<String, Object> paramMap) {
        SQL sql = new SQL() {{
            String startDt = CommonUtil.validOneNull(paramMap, "startDt");
            String endDt = CommonUtil.validOneNull(paramMap, "endDt");
            ArrayList<String> eventKind = CommonUtil.valiArrNull(paramMap, "eventKind");

            SELECT("v4.code_name AS name, count(*) AS value");
            FROM("t_event t1" +
                    "    INNER JOIN t_facility t2 ON t1.facility_seq = t2.facility_seq" +
                    "    INNER JOIN t_station t3 ON t1.station_seq = t3.station_seq" +
                    "    INNER JOIN v_event_kind v1 ON t1.event_kind = v1.code_seq" +
                    "    INNER JOIN v_event_grade v2 ON t1.event_grade = v2.code_seq" +
                    "    INNER JOIN v_event_proc_stat v3 ON t1.event_proc_stat = v3.code_seq" +
                    "    INNER JOIN v_administ v4 ON t2.administ_zone = v4.code_value");

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

    public String selectListOptQry(Map<String, Object> paramMap){
        SQL sql = new SQL() {{
            String keyword = CommonUtil.validOneNull(paramMap, "keyword");
            String start = CommonUtil.validOneNull(paramMap, "start");
            String length = CommonUtil.validOneNull(paramMap, "length");
            String startDt = CommonUtil.validOneNull(paramMap, "startDt");
            String endDt = CommonUtil.validOneNull(paramMap, "endDt");
            ArrayList administZone = CommonUtil.valiArrNull(paramMap, "administZone");
            String sigCode = CommonUtil.validOneNull(paramMap, "sigCode");

            SELECT("t1.facility_opt_name," +
                    "t1.facility_opt_value, " +
                    "to_char(t1.insert_dt, 'YYYY-MM-DD HH24:MI:SS') AS insert_dt, " +
                    "t3.station_name, " +
                    "v1.code_name AS facility_kind, " +
                    "v2.code_name AS station_kind, " +
                    "v3.code_name AS administ_zone_name");
            FROM("t_facility_opt t1\n" +
                    "INNER JOIN t_facility t2 on t1.facility_seq = t2.facility_seq " +
                    "INNER JOIN t_station t3 on t2.station_seq = t3.station_seq " +
                    "INNER JOIN v_facility_kind v1 on t2.facility_kind = v1.code_seq " +
                    "INNER JOIN v_station_kind v2 on t3.station_kind = v2.code_seq " +
                    "INNER JOIN v_administ v3 ON t2.administ_zone = v3.code_value");

            if (!keyword.equals("")) {
                WHERE("t3.station_name LIKE '%" + keyword + "%'");
            }
            if (!startDt.equals("")) {
                WHERE("t1.insert_dt >= to_timestamp('" + startDt + "', 'YYYY-MM-DD HH24:MI:SS')");
            }
            if (!endDt.equals("")) {
                WHERE("t1.insert_dt <= to_timestamp('" + endDt + "', 'YYYY-MM-DD HH24:MI:SS')");
            }
            if (sigCode != null && !sigCode.isEmpty()){
                WHERE("SUBSTRING(v3.code_value, 0, 6) = '" + sigCode + "'");
            }
            if (administZone != null && !administZone.isEmpty()) {
                WHERE( "v3.code_value" + SqlUtil.getWhereInStr(administZone));
            }

            ORDER_BY("t1.insert_dt desc");

            if (!start.equals("") && !length.equals("")) {
                LIMIT(length);
                OFFSET(start);
            }
        }};
        return sql.toString();
    }

    public String selectCountOptQry(Map<String, Object> paramMap) {
        paramMap.remove("start");
        SQL sql = new SQL(){{
            SELECT("COUNT(*) count");
            FROM("(" + selectListOptQry(paramMap) + ") t");
        }};
        return sql.toString();
    }

    public String selectSumOptQry(Map<String, Object> paramMap) {
        Map<String, String> timePatternMap = new HashMap<>();
        timePatternMap.put("hour", "YYYY/MM/DD HH24");
        timePatternMap.put("day", "YYYY/MM/DD");
        timePatternMap.put("week", "YYYY/MM-W");
        timePatternMap.put("month", "YYYY/MM");

        String startDt = CommonUtil.validOneNull(paramMap, "startDt");
        String endDt = CommonUtil.validOneNull(paramMap, "endDt");
        String unit = CommonUtil.validOneNull(paramMap, "unit");
        String optName = CommonUtil.validOneNull(paramMap, "optName");
        String timePattern = timePatternMap.get(unit);
        ArrayList<String> administZone = CommonUtil.valiArrNull(paramMap, "administZone");

        SQL sql = new SQL() {{
            SELECT("to_char(t1.insert_dt, '" + timePattern + "') as x_axis, sum(facility_opt_value::int) as value");
            FROM("t_facility_opt t1");
            INNER_JOIN("t_facility t2 on t1.facility_seq = t2.facility_seq");
            INNER_JOIN("v_administ v1 ON t2.administ_zone = v1.code_value");
            WHERE("t1.insert_dt notnull and facility_opt_name = '"+ optName +"'");


            if (!startDt.equals("")) {
                WHERE("t1.insert_dt >= to_timestamp('" + startDt + "', 'YYYY-MM-DD HH24:MI:SS')");
            }

            if (!endDt.equals("")) {
                WHERE("t1.insert_dt <= to_timestamp('" + endDt + "', 'YYYY-MM-DD HH24:MI:SS')");
            }

            if (administZone != null && !administZone.isEmpty()) {
                WHERE("v1.code_value" + SqlUtil.getWhereInStr(administZone));
            }

            GROUP_BY("x_axis");
        }};

        SQL dataSql = new SQL() {{
            SELECT("t_date AS x_axis, coalesce(value, 0) AS value, SUM(coalesce(value, 0)) OVER (ORDER BY t_date) acc_value");
            // 시간대 생성
            String startDtT = "";
            String endDtT = "";

            if (startDt.equals("")) {
                startDtT = "now()::date - interval '30 " + unit + "'";
            } else {
                startDtT = "'" + startDt + "'";
            }

            if (endDt.equals("")) {
                endDtT = "now()::date";
            } else {
                endDtT = "'" + endDt + "'";
            }
            FROM("(select(to_char(generate_series(" + startDtT + ", " + endDtT + ", '1 " + unit + "'::interval), '" + timePattern + "'))::text as t_date) date_t");
            LEFT_OUTER_JOIN("(" + sql.toString() +") t on date_t.t_date = t.x_axis");
            ORDER_BY("t_date");
        }};

        return dataSql.toString();
    }

    public String selectAvgOptQry(Map<String, Object> paramMap) {
        Map<String, String> timePatternMap = new HashMap<>();
        timePatternMap.put("hour", "YYYY/MM/DD HH24");
        timePatternMap.put("day", "YYYY/MM/DD D");
        timePatternMap.put("week", "YYYY/MM-W");
        timePatternMap.put("month", "YYYY/MM");

        Map<String, String> avgTimePatternMap = new HashMap<>();
        avgTimePatternMap.put("hour", "HH24");
        avgTimePatternMap.put("day", "D");
        avgTimePatternMap.put("week", "W");
        avgTimePatternMap.put("month", "MM");

        String startDt = CommonUtil.validOneNull(paramMap, "startDt");
        String endDt = CommonUtil.validOneNull(paramMap, "endDt");
        String unit = CommonUtil.validOneNull(paramMap, "unit");
        String timePattern = timePatternMap.get(unit);
        String avgTimePattern = avgTimePatternMap.get(unit);
        ArrayList<String> eventKind = CommonUtil.valiArrNull(paramMap, "eventKind");
        ArrayList<String> administZone = CommonUtil.valiArrNull(paramMap, "administZone");

        SQL sql = new SQL() {{
            SQL subQry1 = new SQL() {{
                SELECT("to_char(t1.insert_dt, '" + timePattern + "') AS x_axis," +
                        "   count(case when v2.code_value = '10' then 1 end) urgent," +
                        "   count(case when v2.code_value = '20' then 1 end) caution");
                FROM("t_event t1" +
                        "    INNER JOIN t_facility t2 ON t1.facility_seq = t2.facility_seq" +
                        "    INNER JOIN t_station t3 ON t1.station_seq = t3.station_seq" +
                        "    INNER JOIN v_event_kind v1 ON t1.event_kind = v1.code_seq" +
                        "    INNER JOIN v_event_grade v2 ON t1.event_grade = v2.code_seq" +
                        "    INNER JOIN v_event_proc_stat v3 ON t1.event_proc_stat = v3.code_seq" +
                        "    INNER JOIN v_administ v4 ON t2.administ_zone = v4.code_value");
                if (!startDt.equals("")) {
                    WHERE("t1.insert_dt >= to_timestamp('" + startDt + "', 'YYYY-MM-DD HH24:MI:SS')");
                }
                if (!endDt.equals("")) {
                    WHERE("t1.insert_dt <= to_timestamp('" + endDt + "', 'YYYY-MM-DD HH24:MI:SS')");
                }
                if (eventKind != null && !eventKind.isEmpty()) {
                    WHERE("v1.code_value" + SqlUtil.getWhereInStr(eventKind));
                }

                if (administZone != null && !administZone.isEmpty()) {
                    WHERE("v4.code_value" + SqlUtil.getWhereInStr(administZone));
                }

                GROUP_BY("to_char(t1.insert_dt, '" + timePattern + "')");
            }};

            SELECT("to_char(to_timestamp(x_axis, '" + timePattern + "'), '" + avgTimePattern + "') AS x_axis" +
                    "     , min(urgent)  AS min_urgent" +
                    "     , max(urgent)  AS max_urgent" +
                    "     , min(caution) AS min_caution" +
                    "     , max(caution) AS max_caution" +
                    "     , avg(urgent)  AS avg_urgent" +
                    "     , avg(caution) AS avg_caution");
            FROM("(" + subQry1.toString() + ")t");
            GROUP_BY("to_char(to_timestamp(x_axis, '" + timePattern + "'), '" + avgTimePattern + "')");
        }};

        SQL dateSql = new SQL() {{
            SELECT("date_t.t_date AS x_axis, t.min_urgent, t.max_urgent, t.min_caution, t.max_caution, t.avg_urgent, t.avg_caution");
            // 시간대 생성
            String startDtT = "";
            String endDtT = "";
            if (startDt.equals("")) {
                startDtT = "now()::date - interval '30 " + unit + "'";
            } else {
                startDtT = "'" + startDt + "'";
            }

            if (endDt.equals("")) {
                endDtT = "now()::date";
            } else {
                endDtT = "'" + endDt + "'";
            }
            FROM("(select(to_char(generate_series(" + startDtT + ", " + endDtT + ", '1 " + unit + "'::interval), '" + avgTimePattern + "'))::text as t_date group by t_date) date_t");
            LEFT_OUTER_JOIN("(" + sql.toString() + ") t on date_t.t_date = t.x_axis");
            ORDER_BY("x_axis");
        }};

        return dateSql.toString();
    }

    public String selectMapOptQry(Map<String, Object> paramMap) {
        SQL sql = new SQL() {{
            String startDt = CommonUtil.validOneNull(paramMap, "startDt");
            String endDt = CommonUtil.validOneNull(paramMap, "endDt");
            ArrayList<String> eventKind = CommonUtil.valiArrNull(paramMap, "eventKind");

            SELECT("v4.code_name AS name, count(*) AS value");
            FROM("t_event t1" +
                    "    INNER JOIN t_facility t2 ON t1.facility_seq = t2.facility_seq" +
                    "    INNER JOIN t_station t3 ON t1.station_seq = t3.station_seq" +
                    "    INNER JOIN v_event_kind v1 ON t1.event_kind = v1.code_seq" +
                    "    INNER JOIN v_event_grade v2 ON t1.event_grade = v2.code_seq" +
                    "    INNER JOIN v_event_proc_stat v3 ON t1.event_proc_stat = v3.code_seq" +
                    "    INNER JOIN v_administ v4 ON t2.administ_zone = v4.code_value");

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
