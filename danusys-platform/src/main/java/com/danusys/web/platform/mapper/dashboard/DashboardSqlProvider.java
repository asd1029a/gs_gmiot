package com.danusys.web.platform.mapper.dashboard;

import com.danusys.web.commons.app.CommonUtil;
import org.apache.ibatis.jdbc.SQL;

import java.util.Map;

public class DashboardSqlProvider {

    public String selectStatusCnt1(Map<String, Object> paramMap) {
        String codeSig = CommonUtil.validOneNull(paramMap, "codeSig");
        SQL sql = new SQL() {{
            SELECT("ts.station_seq" +
                    ", ts.station_name as name" +
                    ", '승객 카운트(1시간내)' as sub_name" +
                    ", coalesce(SUM(tfo.facility_opt_value::integer),0) as value" +
                    ", '명' as unit");
            FROM("t_station ts");
            INNER_JOIN("t_facility tf ON ts.station_seq = tf.station_seq");
            INNER_JOIN("t_facility_opt tfo ON tf.facility_seq = tfo.facility_seq");
            WHERE("ts.administ_zone LIKE '"+codeSig+"%'");
            WHERE("tfo.facility_opt_name = 'floating_population'");
            WHERE("to_char(tfo.insert_dt,'YYYYMMDDHH24') between to_char(now() - interval '1 hour','YYYYMMDDHH24') and to_char(now(),'YYYYMMDDHH24')");
            GROUP_BY("ts.station_seq");
        }};
        return sql.toString();
    }

    public String selectKindStatusCnt(Map<String, Object> paramMap) {
        String codeSig = CommonUtil.validOneNull(paramMap, "codeSig");
        String stationKind = CommonUtil.validOneNull(paramMap, "stationKind");
        String subName = CommonUtil.validOneNull(paramMap, "subName");
        SQL sql = new SQL() {{
            SELECT("ts.station_seq" +
                    ", ts.station_name as name" +
                    ", '"+subName+"' as sub_name" +
                    ", coalesce(SUM(tfo.facility_opt_value::integer),0) as value" +
                    ", '명' as unit");
            FROM("t_station ts");
            INNER_JOIN("t_facility tf ON ts.station_seq = tf.station_seq");
            INNER_JOIN("t_facility_opt tfo ON tf.facility_seq = tfo.facility_seq");
            WHERE("ts.administ_zone LIKE '"+codeSig+"%'");
            WHERE("tfo.facility_opt_name = 'floating_population'");
            WHERE("ts.station_kind = '"+stationKind+"'");
            WHERE("to_char(tfo.insert_dt,'YYYYMMDDHH24') between to_char(now() - interval '1 hour','YYYYMMDDHH24') and to_char(now(),'YYYYMMDDHH24')");
            GROUP_BY("ts.station_seq");
        }};
        return sql.toString();
    }

    public String selectTroubleFacility(Map<String, Object> paramMap) {
        String codeSig = CommonUtil.validOneNull(paramMap, "codeSig");
        String stationKind = CommonUtil.validOneNull(paramMap, "stationKind");
        SQL sql = new SQL() {{
            SELECT("(select code_name from v_facility_kind where code_seq = tf.facility_kind)||' 통신장애' as name" +
                    ", sum(case when to_char(tfa.insert_dt,'YYYYMMDDHH24') > to_char(now() - interval '1 hour','YYYYMMDDHH24') then 1 else 0 end) as value" +
                    ", count(1) as total_cnt" +
                    ", '/' as unit" +
                    ", '(1시간내/누적)' as sub_name");
            FROM("t_station ts");
            INNER_JOIN("t_facility tf ON ts.station_seq = tf.station_seq");
            INNER_JOIN("t_facility_active_log tfa ON tfa.facility_seq = tf.facility_seq");
            WHERE("not tfa.facility_active_check");
            WHERE("ts.administ_zone LIKE '"+codeSig+"%'");
            WHERE("ts.station_kind = '"+stationKind+"'");
            GROUP_BY("tf.facility_kind");
        }};
        return sql.toString();
    }

    public String selectEventCount(Map<String, Object> paramMap) {
        String codeSig = CommonUtil.validOneNull(paramMap, "codeSig");
        String eventKind = CommonUtil.validOneNull(paramMap, "eventKind");
        String name = CommonUtil.validOneNull(paramMap, "name");
        String subName = CommonUtil.validOneNull(paramMap, "subName");
        SQL sql = new SQL() {{
            SELECT("'"+name+"' as name" +
                    ", '"+subName+"' as sub_name" +
                    ", sum(case when to_char(te.event_start_dt,'YYYYMMDDHH24') > to_char(now() - interval '1 hour','YYYYMMDDHH24') then 1 else 0 end) as value" +
                    ", count(1) as total_cnt" +
                    ", '/' as unit");
            FROM("t_station ts");
            INNER_JOIN("t_event te ON ts.station_seq = te.station_seq");
            WHERE("ts.administ_zone LIKE '"+codeSig+"%'");
            WHERE("te.event_kind = '"+eventKind+"'");
        }};
        return sql.toString();
    }

    public String selectStationByPeopleCntList(Map<String, Object> paramMap) {
        String codeSig = CommonUtil.validOneNull(paramMap, "codeSig");
        //String codeSig = "47210";

        SQL sql = new SQL() {{
            SELECT("ts.station_seq, ts.station_name");
            FROM("t_station ts");
            INNER_JOIN("t_facility tf ON ts.station_seq = tf.station_seq");
            INNER_JOIN("t_facility_opt tfo ON tf.facility_seq = tfo.facility_seq");
            WHERE("ts.administ_zone LIKE '"+codeSig+"%'");
            WHERE("tfo.facility_opt_name = 'floating_population'");
            WHERE("to_char(tfo.insert_dt,'YYYYMMDDHH24') BETWEEN to_char(now() - interval '12 hour','YYYYMMDDHH24') AND to_char(now() - interval '1 hour','YYYYMMDDHH24')");
            GROUP_BY("ts.station_seq");
        }};
        return sql.toString();
    }

    public String selectPeopleCntByStationList(Map<String, Object> paramMap) {
        String stationSeq = CommonUtil.validOneNull(paramMap, "stationSeq");
        String codeSig = CommonUtil.validOneNull(paramMap, "codeSig");
        //String codeSig = "47210";

        SQL sqlTemp = new SQL() {{
            SELECT(" unnest(ARRAY[to_char(now() - interval '12 hour','YYYYMMDDHH24')" +
                    ",to_char(now() - interval '11 hour','YYYYMMDDHH24')" +
                    ",to_char(now() - interval '10 hour','YYYYMMDDHH24')" +
                    ",to_char(now() - interval '9 hour','YYYYMMDDHH24')" +
                    ",to_char(now() - interval '8 hour','YYYYMMDDHH24')" +
                    ",to_char(now() - interval '7 hour','YYYYMMDDHH24')" +
                    ",to_char(now() - interval '6 hour','YYYYMMDDHH24')" +
                    ",to_char(now() - interval '5 hour','YYYYMMDDHH24')" +
                    ",to_char(now() - interval '4 hour','YYYYMMDDHH24')" +
                    ",to_char(now() - interval '3 hour','YYYYMMDDHH24')" +
                    ",to_char(now() - interval '2 hour','YYYYMMDDHH24')" +
                    ",to_char(now() - interval '1 hour','YYYYMMDDHH24')]) insert_dt");
        }};
        SQL sqlTemp2 = new SQL() {{
            SELECT("to_char(tfo.insert_dt,'YYYYMMDDHH24') insert_dt, sum(facility_opt_value::integer) time_cnt");   //kW
            FROM("t_station ts");
            INNER_JOIN("t_facility tf ON ts.station_seq = tf.station_seq");
            INNER_JOIN("t_facility_opt tfo ON tf.facility_seq = tfo.facility_seq");
            WHERE("ts.administ_zone LIKE '"+codeSig+"%'");
            WHERE("tfo.facility_opt_name = 'floating_population'");
            WHERE("ts.station_seq = '"+stationSeq+"'");
            WHERE("to_char(tfo.insert_dt,'YYYYMMDDHH24') BETWEEN to_char(now() - interval '12 hour','YYYYMMDDHH24') AND to_char(now() - interval '1 hour','YYYYMMDDHH24')");
            GROUP_BY("to_char(tfo.insert_dt,'YYYYMMDDHH24')");
            ORDER_BY("to_char(tfo.insert_dt,'YYYYMMDDHH24') ASC");
        }};
        SQL sql = new SQL() {{
            SELECT("a.insert_dt, coalesce(b.time_cnt,0) time_cnt");
            FROM(" ("+sqlTemp+") a");
            LEFT_OUTER_JOIN("("+sqlTemp2+") b ON b.insert_dt = a.insert_dt");
        }};

        return sql.toString();
    }

    public String selectStationKindList(Map<String, Object> paramMap) {
        String codeSig = CommonUtil.validOneNull(paramMap, "codeSig");
        SQL sql = new SQL() {{
            SELECT("(select code_name from v_station_kind where code_seq = station_kind), station_kind as code_seq");
            FROM("t_station");
            WHERE("administ_zone LIKE '"+codeSig+"%'");
            GROUP_BY("station_kind");
        }};
        return sql.toString();
    }

    public String selectStationCntByDong(Map<String, Object> paramMap) {
        String codeSig = CommonUtil.validOneNull(paramMap, "codeSig");
        String codeSeq = CommonUtil.validOneNull(paramMap, "codeSeq");
        SQL sql = new SQL() {{
            SELECT("va.code_name as station_name, count(ts.*) as station_cnt");
            FROM("v_administ va");
            LEFT_OUTER_JOIN("t_station ts" +
                    " ON va.code_seq::text = ts.administ_zone" +
                    " AND ts.station_kind::text = '"+codeSeq+"'");
            WHERE("va.code_seq::text LIKE '"+codeSig+"%'");
            GROUP_BY("va.code_seq::text, va.code_name");
        }};
        return sql.toString();
    }

    public String selectAirPollution(Map<String, Object> paramMap) {
        String codeSig = CommonUtil.validOneNull(paramMap, "codeSig");
        SQL sql = new SQL() {{
            SELECT("ts.station_name as sensor_name" +
                    ",sum(case tfo.facility_opt_name when 'PM10' then tfo.facility_opt_value::numeric end) pm10_value24" +
                    ",sum(case tfo.facility_opt_name when 'PM2.5' then tfo.facility_opt_value::numeric end) pm25_value24" +
                    ",sum(case tfo.facility_opt_name when 'temperature' then tfo.facility_opt_value::numeric end) temperature" +
                    ",sum(case tfo.facility_opt_name when 'humidity' then tfo.facility_opt_value::numeric end) humidity");
            FROM("t_station ts");
            INNER_JOIN("t_facility tf ON ts.station_seq = tf.station_seq");
            INNER_JOIN("t_facility_opt tfo ON tf.facility_seq = tfo.facility_seq");
            WHERE("ts.administ_zone LIKE '"+codeSig+"%'");
            WHERE("tfo.facility_opt_name IN ('PM10','PM2.5','temperature','humidity')");
            WHERE("tfo.facility_opt_value != ''");
            GROUP_BY("ts.station_seq");
        }};
        return sql.toString();
    }

    public String getDroneCabinetStatus(Map<String, Object> paramMap) {
        SQL sql = new SQL() {{
            SELECT(" '' fly_cnt" +
                    ",'' fire_cnt" +
                    ",sum(case when tfo.facility_opt_name = 'amn_status'and tfo.facility_opt_value = '위험' then 1 else 0 end) amn_danger_cnt" +
                    ",sum(case when tfo.facility_opt_name = 'amn_status'and tfo.facility_opt_value = '경고' then 1 else 0 end) amn_warn_cnt" +
                    ",sum(case when tfo.facility_opt_name = 'oam_status'and tfo.facility_opt_value = '위험' then 1 else 0 end) oam_danger_cnt" +
                    ",sum(case when tfo.facility_opt_name = 'oam_status'and tfo.facility_opt_value = '경고' then 1 else 0 end) oam_warn_cnt");
            FROM("t_facility tf");
            INNER_JOIN("t_facility_opt tfo ON tf.facility_seq = tfo.facility_seq");
            WHERE("tf.administ_zone LIKE '45210%'");
            WHERE("tf.facility_kind = '43'");
        }};
        return sql.toString();
    }

    public String selectCabinetRank(Map<String, Object> paramMap) {
        SQL sql = new SQL() {{
            SELECT("* FROM (" +
                    "SELECT" +
                        " min(case tfo.facility_opt_value when '위험' then 1 when '경고' then 2 when '주의' then 3 when '정상' then 4 else 9 end) rank" +
                        ",tf.facility_seq" +
                        ", tf.station_seq" +
                        ", tf.facility_name" +
                        ", max(case tfo.facility_opt_name when 'wat_tot' then tfo.facility_opt_value end ) wat_tot" +
                        ", max(case tfo.facility_opt_name when 'amn_status' then tfo.facility_opt_value end) amn_status" +
                        ", max(case tfo.facility_opt_name when 'oam_status' then tfo.facility_opt_value end) oam_status" +
                        ", max(case tfo.facility_opt_name when 'am_use' then tfo.facility_opt_value end) am_use");
                FROM("t_facility tf");
                INNER_JOIN("t_facility_opt tfo ON tf.facility_seq = tfo.facility_seq");
                WHERE("tf.administ_zone LIKE '45210%'");
                WHERE("tf.facility_kind = '43'");
                GROUP_BY("tf.facility_seq) a");
            ORDER_BY("a.rank");
            LIMIT(5);
        }};
        return sql.toString();
    }
}
