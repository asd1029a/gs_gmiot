package com.danusys.web.platform.mapper.dashboard;

import com.danusys.web.commons.app.CommonUtil;
import org.apache.ibatis.jdbc.SQL;

import java.util.Map;

public class DashboardSqlProvider {

    public String selectStatusCnt1(Map<String, Object> paramMap) {
        SQL sql = new SQL() {{
            SELECT("'스마트가로등1' as name, '승객 카운트' as sub_name, '14' as value, '명' as unit" +
                    " union all" +
                    " select '스마트가로등2' as name, '승객 카운트' as sub_name, '12' as value, '명' as unit" +
                    " union all" +
                    " select '스마트가로등3' as name, '승객 카운트' as sub_name, '6' as value, '명' as unit");

            /*
select *
from t_facility tf
inner join t_facility_opt tfo
on tf.facility_seq = tfo.facility_seq
AND tfo.facility_opt_name = 'floating_population'
            */
            //FROM("t_event");
            /*if (keyword != null && !keyword.equals("")) {
                WHERE("v1.code_name LIKE '%" + keyword + "%'");
            }*/
        }};
        return sql.toString();
    }

    public String selectTroubleBus(Map<String, Object> paramMap) {
        SQL sql = new SQL() {{
            SELECT("'스마트버스정류장 통신장애' as name, '' as sub_name, '2' as value, '8' as total_cnt, '/' as unit" +
                    " union all" +
                    " select '스마트버스정류장 통신장애2' as name, '' as sub_name, '1' as value, '12' as total_cnt, '/' as unit");

            //FROM("t_event");
            /*if (keyword != null && !keyword.equals("")) {
                WHERE("v1.code_name LIKE '%" + keyword + "%'");
            }*/
        }};
        return sql.toString();
    }
    public String selectTroublePole(Map<String, Object> paramMap) {
        SQL sql = new SQL() {{
            SELECT("'스마트폴 통신장애' as name" +
                    ", '' as sub_name" +
                    ", '3' as value" +
                    ", '11' as total_cnt" +
                    ", '/' as unit");
        }};
        return sql.toString();
    }

    public String selectEventDropAttack(Map<String, Object> paramMap) {
        String codeSig = CommonUtil.validOneNull(paramMap, "codeSig");
        SQL sql = new SQL() {{
            SELECT("'유동인구 이벤트' as name" +
                    ", '쓰러짐 감지' as sub_name" +
                    ", count(te.*) as value" +
                    ", '' as total_cnt" +
                    ", '건' as unit");
            FROM("t_station ts");
            INNER_JOIN("t_event te ON ts.station_seq = te.station_seq");
            WHERE("ts.administ_zone LIKE '"+codeSig+"%'");
            WHERE("te.event_kind = '65'");
        }};
        return sql.toString();
    }

    public String selectEventSuspectDetection(Map<String, Object> paramMap) {
        SQL sql = new SQL() {{
            SELECT("'지능형 카메라 이벤트' as name" +
                    ", '용의자 검출' as sub_name" +
                    ", '1' as value" +
                    //", '10' as total_cnt" +
                    ", '건' as unit");
        }};
        return sql.toString();
    }

    public String selectEventPole(Map<String, Object> paramMap) {
        SQL sql = new SQL() {{
            SELECT("'스마트폴 이벤트' as name" +
                    ", '전원이상' as sub_name" +
                    ", '3' as value" +
                    ", '' as total_cnt" +
                    ", '건' as unit");   //kW
        }};
        return sql.toString();
    }

    public String selectEventFire(Map<String, Object> paramMap) {
        String codeSig = CommonUtil.validOneNull(paramMap, "codeSig");
        SQL sql = new SQL() {{
            SELECT("'유동인구 이벤트' as name" +
                    ", '화재 감지' as sub_name" +
                    ", count(te.*) as value" +
                    ", '' as total_cnt" +
                    ", '건' as unit");
            FROM("t_station ts");
            INNER_JOIN("t_event te ON ts.station_seq = te.station_seq");
            WHERE("ts.administ_zone LIKE '"+codeSig+"%'");
            WHERE("te.event_kind = '66'");
        }};
        return sql.toString();
    }

    public String selectEventMissingPerson(Map<String, Object> paramMap) {
        SQL sql = new SQL() {{
            SELECT("'지능형 카메라 이벤트' as name" +
                    ", '실종자 검출' as sub_name" +
                    ", '2' as value" +
                    ", '10' as total_cnt" +
                    ", '/' as unit");   //kW
        }};
        return sql.toString();
    }

    public String selectStationList(Map<String, Object> paramMap) {
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
            SELECT("'스마트가로등1' as sensor_name, '100' as pm25_value24, '90' as pm10_value24, '20' as temperature, '10' as humidity" +
                    " union all" +
                    " select '스마트가로등2' as sensor_name, '120' as pm25_value24, '70' as pm10_value24, '21' as temperature, '20' as humidity" +
                    " union all" +
                    " select '스마트가로등3' as sensor_name, '40' as pm25_value24, '80' as pm10_value24, '22' as temperature, '30' as humidity");
        }};
        return sql.toString();
    }

    public String getDronCabinetStatus(Map<String, Object> paramMap) {
        SQL sql = new SQL() {{
            SELECT(" '' fly_cnt" +
                    ",'' fire_cnt" +
                    ", sum(case when tfo.facility_opt_name = 'amn_status'and tfo.facility_opt_value = '위험' then 1 else 0 end) amn_danger_cnt" +
                    ", sum(case when tfo.facility_opt_name = 'amn_status'and tfo.facility_opt_value = '경고' then 1 else 0 end) amn_warn_cnt" +
                    ", sum(case when tfo.facility_opt_name = 'oam_status'and tfo.facility_opt_value = '위험' then 1 else 0 end) oam_danger_cnt" +
                    ", sum(case when tfo.facility_opt_name = 'oam_status'and tfo.facility_opt_value = '경고' then 1 else 0 end) oam_warn_cnt");
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
