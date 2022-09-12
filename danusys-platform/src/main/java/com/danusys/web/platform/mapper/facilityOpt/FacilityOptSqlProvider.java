package com.danusys.web.platform.mapper.facilityOpt;

import com.danusys.web.commons.app.CommonUtil;
import com.danusys.web.commons.app.SqlUtil;
import org.apache.ibatis.jdbc.SQL;
import org.springframework.beans.factory.annotation.Value;

import java.util.ArrayList;
import java.util.Map;

public class FacilityOptSqlProvider {

    public String selectListQry(Map<String, Object> paramMap) {
        String keyword = CommonUtil.validOneNull(paramMap, "keyword");
        String optName = CommonUtil.validOneNull(paramMap, "optName");
        String sigCode = CommonUtil.validOneNull(paramMap, "sigCode"); //지자체 구분용
        ArrayList<String> stationKind = CommonUtil.valiArrNull(paramMap, "stationKind");
        ArrayList<String> facilityKind = CommonUtil.valiArrNull(paramMap, "facilityKind");
        ArrayList<String> administZone = CommonUtil.valiArrNull(paramMap, "administZone"); //동 구분용
        String startDt = CommonUtil.validOneNull(paramMap, "startDt");
        String endDt = CommonUtil.validOneNull(paramMap, "endDt");
        String start = CommonUtil.validOneNull(paramMap, "start");
        String length = CommonUtil.validOneNull(paramMap, "length");

        SQL sql = new SQL() {{
            SELECT("t1.station_seq, t1.station_name, t1.station_kind, t1.administ_zone" +
                    ", t1.station_image, t1.station_compet_dt, t1.station_size" +
                    ", t1.station_material, t1.latitude, t1.longitude, t1.address, t1.remark" +
                    ", t2.facility_name" +
                    ", v1.code_name  AS station_kind_name, v1.code_value AS station_kind_value" +
                    ", v2.code_name AS administ_zone_name" +
                    ", t3.facility_opt_value AS opt_value" +
                    ", v3.code_name AS facility_kind_name, v3.code_value AS facility_kind_value" );
            FROM("t_station t1");
            INNER_JOIN("t_facility t2 ON t1.station_seq = t2.station_seq");
            INNER_JOIN("t_facility_opt t3 ON t2.facility_seq = t3.facility_seq");
            LEFT_OUTER_JOIN("v_station_kind v1 ON t1.station_kind = v1.code_seq");
            LEFT_OUTER_JOIN("v_administ v2 ON t1.administ_zone = v2.code_value");
            LEFT_OUTER_JOIN("v_facility_kind v3 ON t2.facility_kind = v3.code_seq ");
            if (optName != null && !optName.equals("")) {
                WHERE("t3.facility_opt_name = '" + optName +"'");
            }
            if (!stationKind.isEmpty()) {
                WHERE("v1.code_value" + SqlUtil.getWhereInStr(stationKind));
            }
            if (!facilityKind.isEmpty()) {
                WHERE("v3.code_value" + SqlUtil.getWhereInStr(facilityKind));
            }
            if(sigCode != null && !sigCode.isEmpty()){  //지자체 분기를 위한 시군구 구분
                WHERE("substring(v2.code_value,0,6) = '" + sigCode + "'");
            }
            if (!administZone.isEmpty()) {
                WHERE("v2.code_value" + SqlUtil.getWhereInStr(administZone));
            }
            if (keyword != null && !keyword.equals("")) {
                WHERE("(t1.station_name LIKE '%" + keyword + "%'" +
                        " OR t1.station_seq LIKE '%" + keyword + "%'" +
                        " OR t1.address LIKE '%" + keyword + "%'" +
                        ")");
            }
            if (!startDt.equals("")) {
                WHERE("t3.insert_dt >= to_timestamp('" + startDt + "', 'YYYY-MM-DD HH24:MI:SS')");
            }
            if (!endDt.equals("")) {
                WHERE("t3.insert_dt <= to_timestamp('" + endDt + "', 'YYYY-MM-DD HH24:MI:SS')");
            }
            ORDER_BY("t3.insert_dt DESC");
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
           SELECT("COUNT(c1.*) count");
           FROM("(" + selectListQry(paramMap) + ") AS c1");
        }};
        return sql.toString();
    }
}
