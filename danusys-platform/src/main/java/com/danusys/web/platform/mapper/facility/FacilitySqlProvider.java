package com.danusys.web.platform.mapper.facility;

import com.danusys.web.commons.app.CommonUtil;
import com.danusys.web.commons.app.SqlUtil;
import com.danusys.web.platform.dto.request.SignageRequestDto;
import jdk.nashorn.internal.runtime.regexp.joni.encoding.CharacterType;
import org.apache.ibatis.jdbc.SQL;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FacilitySqlProvider {

    public String selectListQry(Map<String, Object> paramMap) {
        String keyword = CommonUtil.validOneNull(paramMap, "keyword");
        ArrayList<String> facilityKind = CommonUtil.valiArrNull(paramMap, "facilityKind");
        String sigCode = CommonUtil.validOneNull(paramMap, "sigCode"); //지자체 구분용
        String stationSeq = CommonUtil.validOneNull(paramMap, "stationSeq"); //개소 SEQ
        ArrayList<String> administZone = CommonUtil.valiArrNull(paramMap, "administZone"); //동 구분용
        ArrayList<String> stationKind = CommonUtil.valiArrNull(paramMap, "stationKind");
        ArrayList<String> status = CommonUtil.valiArrNull(paramMap, "status");
        String start = CommonUtil.validOneNull(paramMap, "start");
        String length = CommonUtil.validOneNull(paramMap, "length");
        String kindCodeViewName = CommonUtil.validOneNull(paramMap, "kindCodeViewName").isEmpty() ? "v_facility_kind" : CommonUtil.validOneNull(paramMap, "kindCodeViewName");


        StringBuilder builder = new StringBuilder();

        SQL sql = new SQL() {{
            builder.append("t1.facility_seq, t1.facility_id, t1.facility_name");
            builder.append(", t1.administ_zone, t1.facility_image");
            builder.append(", t1.facility_instl_info, t1.facility_instl_dt");
            builder.append(", t1.facility_status " +
                    ", CASE" +
                    " WHEN t1.facility_status = 0 THEN '이상'" +
                    " WHEN t1.facility_status = 1 then '정상'" +
                    /*" ELSE '이상' END AS facility_status_name" +*/
                    " END AS facility_status_name" +
                    ", t1.latitude");
            builder.append(", t1.longitude, t1.insert_dt, t3.id AS insert_user_id, t1.alive_check");
            builder.append(", t1.update_user_seq , t4.id AS update_user_id");
            builder.append(", t2.code_value AS facility_kind");
            builder.append(", t2.code_name AS facility_kind_name");
            builder.append(", t5.station_kind, t5.station_name, t5.address");
            builder.append(", t6.code_name AS administ_zone_name");
            builder.append(", t7.code_name AS station_kind_name");
            builder.append(", t7.code_value AS station_kind_value");

            if (facilityKind.contains("smartBusStop")) {
                //TODO 시설물 OPT 정보
//                builder.append(", t9.*");
            }


            SELECT(builder.toString());
            FROM("t_facility t1");
            INNER_JOIN("v_facility_kind t2 on t1.facility_kind = t2.code_seq");
            LEFT_OUTER_JOIN("t_user t3 on t1.insert_user_seq = t3.user_seq");
            LEFT_OUTER_JOIN("t_user t4 on t1.update_user_seq = t4.user_seq");
            LEFT_OUTER_JOIN("t_station t5 on t1.station_seq = t5.station_seq");
            LEFT_OUTER_JOIN("v_administ t6 on t1.administ_zone = t6.code_value");
            LEFT_OUTER_JOIN("v_station_kind t7 on t5.station_kind = t7.code_seq");

            if (facilityKind != null && !facilityKind.isEmpty()) {
                WHERE("t2.code_value" + SqlUtil.getWhereInStr(facilityKind));
            }
            if (sigCode != null && !sigCode.isEmpty()) {
                WHERE("substring(t6.code_value,0,6) = '" + sigCode + "'");
            }
            if (stationSeq != null && !stationSeq.isEmpty()) {
                WHERE("t5.station_seq = " + stationSeq);
            }
            if(administZone != null && !administZone.isEmpty()) {
                WHERE("t6.code_value" + SqlUtil.getWhereInStr(administZone));
            }
            if (stationKind != null && !stationKind.isEmpty()) {
                WHERE("t7.code_value" + SqlUtil.getWhereInStr(stationKind));
            }
            if (keyword != null && !keyword.equals("")) {
                WHERE("(t1.facility_id LIKE '%" + keyword + "%'" +
                        " OR t2.code_name LIKE '%" + keyword + "%'" +
                        " OR t5.station_name LIKE '%" + keyword + "%'" +
                        " OR t6.code_name LIKE '%" + keyword + "%'" +
                        " OR t7.code_name LIKE '%" + keyword + "%'" +
                        ")");
            }
            if (status.size() != 0) {
                WHERE("t1.facility_status" + SqlUtil.getWhereInStr(status));
            }
            ORDER_BY("t1.facility_seq");
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
                    ", count(case when c1.facility_status = 0 then 1 end) AS error_count" +
                    ", count(case when c1.facility_status = 1 then 1 end) AS normal_count");
            /*SELECT("COUNT(c1.*) count" +
                    ", count(case when c1.facility_status = 0 then 1 end) AS not_use_count" +
                    ", count(case when c1.facility_status = 1 then 1 end) AS normal_count" +
                    ", count(case when c1.facility_status = 2 then 1 end) AS error_count");*/
            FROM("(" + selectListQry(paramMap) + ") AS c1");
        }};
        return sql.toString();
    }

    public String selectOneQry(int seq) {
        SQL sql = new SQL() {{
            SELECT("*");
            FROM("t_facility t1");
            INNER_JOIN("t_facility_opt t2 on t1.facility_seq = t2.facility_seq");
            WHERE("t1.facility_seq = " + seq);
        }};
        return sql.toString();
    }

    public String insertQry(Map<String, Object> paramMap) {
        Map<String, Object> qryMap = SqlUtil.getInsertValuesStr(paramMap);

        SQL sql = new SQL() {{
            INSERT_INTO("t_facility");
            VALUES(qryMap.get("columns").toString(), qryMap.get("values").toString());
        }};
        return sql.toString();
    }

    public String insertOptQry(Map<String, Object> paramMap) {
        List<Map<String, Object>> facilityOptList = (List<Map<String, Object>>) paramMap.get("facilityOptList");
        StringBuilder sb = new StringBuilder();
        int index = 0;

        for (Map<String, Object> facilityOpt : facilityOptList) {
            if (index == 0) {
                sb.append("INSERT INTO t_facility_opt (")
                        .append(SqlUtil.getInsertValuesStr(facilityOpt).get("columns").toString())
                        .append(")")
                        .append(" VALUES ");
            }
            sb.append("(")
                    .append(SqlUtil.getInsertValuesStr(facilityOpt).get("values").toString());
            if (index == facilityOptList.size() - 1) {
                sb.append(")");
                break;
            } else {
                sb.append("), ");
            }
            index++;
        }
        return sb.toString();
    }

    public String updateQry(Map<String, Object> paramMap) {
        String facilitySeq = paramMap.get("facilitySeq").toString();

        SQL sql = new SQL() {{
            UPDATE("t_facility");
            SET(SqlUtil.getMultiSetStr(paramMap));
            WHERE("facility_seq =" + facilitySeq);
        }};
        return sql.toString();
    }

    public String updateNullStationSeqQry(Map<String, Object> paramMap) {
        String stationSeq = paramMap.get("stationSeq").toString();

        SQL sql = new SQL() {{
            UPDATE("t_facility");
            SET("station_seq = NULL");
            WHERE("station_seq = " + stationSeq);
        }};
        return sql.toString();
    }

    public String updateStationSeqQry(Map<String, Object> paramMap) {
        ArrayList<String> facilitySeqList = CommonUtil.valiArrNull(paramMap, "facilitySeqList");

        SQL sql = new SQL() {{
            UPDATE("t_facility");
            SET("station_seq = " + paramMap.get("stationSeq"));
            WHERE("facility_seq " + SqlUtil.getWhereInStr(facilitySeqList));
        }};
        return sql.toString();
    }

    public String updateOptQry(Map<String, Object> paramMap) {
        String facilitySeq = paramMap.get("facilitySeq").toString();

        SQL sql = new SQL() {{
            UPDATE("t_facility_opt");
            SET(SqlUtil.getMultiSetStr(paramMap));
            WHERE("facility_seq =" + facilitySeq);
        }};
        return sql.toString();
    }

    public String deleteQry(int seq) {
        SQL sql = new SQL() {{
            DELETE_FROM("t_facility");
            WHERE("facility_seq =" + seq);
        }};
        return sql.toString();
    }

    public String deleteOptQry(Map<String, Object> paramMap) {
        String facilityOptType = CommonUtil.validOneNull(paramMap, "facilityOptType");
        List<Integer> delFacilitySeqList = (List<Integer>) paramMap.get("delFacilitySeqList");
        List<Integer> facilitySeqList = (List<Integer>) paramMap.get("facilitySeqList");
        List<String> ignoreDeleteOptList = (List<String>) paramMap.get("ignoreDeleteOptList");
        List<Integer> onlyDelList = new ArrayList<>();
        if (facilitySeqList != null) {
            for (Integer d :delFacilitySeqList) {
                if (!(facilitySeqList.contains(d.toString()))) {
                    onlyDelList.add(d);
                }
            }
        }

        String ignoreDeleteOptListStr = "";
        String delFacilitySeqListStr = delFacilitySeqList
                .stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
        String onlyDelString = "";
        if (!onlyDelList.isEmpty()) {
            onlyDelString = onlyDelList
                    .stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(","));
        }

        StringBuilder sb = new StringBuilder();
        int index = 0;
        if (ignoreDeleteOptList != null && !ignoreDeleteOptList.isEmpty()) {
            for (String ignoreDeleteOpt : ignoreDeleteOptList) {
                if (index == ignoreDeleteOptList.size() - 1) {
                    sb.append("'" + ignoreDeleteOpt + "'");
                    break;
                } else {
                    sb.append("'" + ignoreDeleteOpt + "'").append(", ");
                }
                index++;
            }
        }

        String finalOnlyDelString = onlyDelString;
        SQL sql = new SQL() {{
            if (facilitySeqList != null) {
                if (facilitySeqList.size() >= delFacilitySeqList.size() && (finalOnlyDelString == "")) {
                    DELETE_FROM("t_facility_opt t1 ");
                    WHERE("t1.facility_seq IN ( " + delFacilitySeqListStr + " )");
                    if (ignoreDeleteOptList != null && !ignoreDeleteOptList.isEmpty()) {
                        WHERE("t1.facility_opt_name NOT IN ( " + sb.toString() + " )");
                    }
                    WHERE("t1.facility_opt_type = (" +
                            "SELECT code_seq " +
                            "FROM v_facility_opt_type " +
                            "WHERE code_value = '" + facilityOptType + "')");
                } else {
                    DELETE_FROM("t_facility_opt t1 ");
                    if (finalOnlyDelString != "") {
                        WHERE("t1.facility_seq IN ( " + finalOnlyDelString + " )");
                    } else {
                        WHERE("t1.facility_seq IN ( " + delFacilitySeqListStr + " )");
                    }
                    WHERE("t1.facility_opt_type = (" +
                            "SELECT code_seq " +
                            "FROM v_facility_opt_type " +
                            "WHERE code_value = '" + facilityOptType + "')");
                }
            } else {
                DELETE_FROM("t_facility_opt t1 ");
                WHERE("t1.facility_seq IN ( " + delFacilitySeqListStr + " )");
                if (ignoreDeleteOptList != null && !ignoreDeleteOptList.isEmpty()) {
                    WHERE("t1.facility_opt_name NOT IN ( " + sb.toString() + " )");
                }
                WHERE("t1.facility_opt_type = (" +
                        "SELECT code_seq " +
                        "FROM v_facility_opt_type " +
                        "WHERE code_value = '" + facilityOptType + "')");
            }
        }};
        return sql.toString();
    }

    public String selectListFacilityForStationQry(Map<String, Object> paramMap) {
        String facilityKind = CommonUtil.validOneNull(paramMap, "facilityKind");
        SQL sql = new SQL() {{
            SELECT("t1.facility_seq, t1.facility_id, t1.administ_zone, t1.facility_image" +
                    ", t1.facility_instl_info, t1.facility_instl_dt, t1.facility_status" +
                    ", t1.latitude, t1.longitude, t1.insert_dt, t2.code_value AS facility_kind" +
                    ", t2.code_name AS facility_kind_name, t6.code_name AS administ_zone_name");
            FROM("t_facility t1");
            INNER_JOIN("v_facility_kind t2 on t1.facility_kind = t2.code_seq" +
                    " AND t2.code_value = '" + facilityKind + "'");
            LEFT_OUTER_JOIN("t_station t5 on t1.station_seq = t5.station_seq");
//            현재 뷰테이블과 시설물 구역이 맞지 않아 임시로 조회
            LEFT_OUTER_JOIN("v_administ t6 on t1.administ_zone = t6.code_value");
//            LEFT_OUTER_JOIN("t_area_emd t6 on t1.administ_zone = t6.emd_cd");
            if("mod".equals(paramMap.get("type"))) {
                WHERE("t1.station_seq IS NULL " +
                        "OR t1.station_seq = " + paramMap.get("stationSeq"));
            } else if("add".equals(paramMap.get("type"))){
                WHERE("NOT EXISTS(SELECT *" +
                        " FROM t_station s1" +
                        " WHERE s1.station_seq = t1.station_seq)");
            }
            ORDER_BY("t1.facility_seq");
        }};
        return sql.toString();
    }

    public String selectListFacilityForDimmingQry(Map<String, Object> paramMap) {
        ArrayList<String> facilityKind = CommonUtil.valiArrNull(paramMap, "facilityKind");
        String activeType = CommonUtil.validOneNull(paramMap, "activeType");

        SQL sql = new SQL() {{
            SELECT("t1.facility_seq, t1.facility_id, t1.facility_name" +
                    ", t1.administ_zone, t1.latitude"+
                    ", t1.longitude, t1.insert_dt" +
                    ", t2.code_value AS facility_kind" +
                    ", t2.code_name AS facility_kind_name" +
                    ", t3.code_name AS administ_zone_name");
            FROM("t_facility t1");
            INNER_JOIN("v_facility_kind t2 on t1.facility_kind = t2.code_seq");
            LEFT_OUTER_JOIN("v_administ t3 on t1.administ_zone = t3.code_value");
            WHERE("t2.code_value" + SqlUtil.getWhereInStr(facilityKind));
            String modifyQry = "";
            if ("mod".equals(activeType)) {
                modifyQry = "AND v1.dimming_group_seq::integer != " + paramMap.get("dimmingGroupSeq");
            }
            WHERE("NOT EXISTS (" +
                    "SELECT * " +
                    "FROM v_dimming_group v1 " +
                    "WHERE v1.facility_seq = t1.facility_seq " +
                    modifyQry +
                    ")");
            ORDER_BY("t1.facility_seq");
        }};
        return sql.toString();
    }

    public String selectListFacilityForSignageQry(Map<String, Object> paramMap) {
        SQL sql = new SQL() {{
            SELECT("t1.facility_seq, t1.facility_id" +
                    ", t1.administ_zone, t1.insert_dt" +
                    ", t2.code_value AS facility_kind" +
                    ", t2.code_name AS facility_kind_name" +
                    ", t3.code_name AS administ_zone_name");
            FROM("t_facility t1");
            INNER_JOIN("v_facility_kind t2 on t1.facility_kind = t2.code_seq");
            LEFT_OUTER_JOIN("v_administ t3 on t1.administ_zone = t3.code_value");
            WHERE("t1.facility_kind = 58");
            ORDER_BY("t1.facility_seq");
        }};
        return sql.toString();
    }

    public String selectListFacilityInStationQry(Map<String, Object> paramMap) {

        SQL sql = new SQL() {{
            SELECT("t1.facility_seq");
            FROM("t_facility t1");
            INNER_JOIN("t_station t5 on t1.station_seq = t5.station_seq AND t1.station_seq = " + paramMap.get("stationSeq"));
            ORDER_BY("t1.facility_seq");
        }};
        return sql.toString();
    }

    public String selectListDimmingGroupQry(Map<String, Object> paramMap) {
        String keyword = CommonUtil.validOneNull(paramMap, "keyword");
        String start = CommonUtil.validOneNull(paramMap, "start");
        String length = CommonUtil.validOneNull(paramMap, "length");

        SQL sql = new SQL() {{

            SELECT("v1.dimming_group_seq, v1.dimming_group_name");
            FROM("(" +
                    "SELECT *" +
                    "FROM v_dimming_group" +
                    ") v1");
            if (keyword != null && !keyword.equals("")) {
                WHERE("v1.dimming_group_name LIKE '%" + keyword + "%'");
            }
            GROUP_BY("v1.dimming_group_seq, v1.dimming_group_name");
            if (!start.equals("") && !length.equals("")) {
                LIMIT(length);
                OFFSET(start);
            }
        }};
        return sql.toString();
    }

    public String selectCountDimmingGroupQry(Map<String, Object> paramMap) {
        String keyword = CommonUtil.validOneNull(paramMap, "keyword");

        SQL sql = new SQL() {{

            SELECT("COUNT(s1.*)");
            FROM(
                "(" +
                    "SELECT v1.dimming_group_name " +
                    "FROM (" +
                        "SELECT * " +
                        "FROM v_dimming_group" +
                    ") v1"
            );
            if (keyword != null && !keyword.equals("")) {
                WHERE("v1.dimming_group_name LIKE '%" + keyword + "%'");
            }
            GROUP_BY("v1.dimming_group_name ) s1");
        }};
        return sql.toString();
    }

    public String selectListLampRoadInDimmingGroupQry(Map<String, Object> paramMap) {
        String dimmingGroupSeq = CommonUtil.validOneNull(paramMap, "dimmingGroupSeq");

        SQL sql = new SQL() {
            {
                SELECT("t1.facility_seq, t1.facility_Id, t1.longitude, t1.latitude, t1.administ_zone" +
                        ", v1.dimming_group_name, v1.dimming_group_seq, v1.keep_bright_time" +
                        ", v1.max_bright_time, v1.min_bright_time, v1.dimming_time_zone");
                FROM("t_facility t1");
                INNER_JOIN("v_dimming_group v1 on t1.facility_seq = v1.facility_seq");
                WHERE("v1.dimming_group_seq::integer = " + dimmingGroupSeq);
            }
        };
        return sql.toString();
    }

    public String selectOneLastDimmingGroupSeqQry() {
        SQL sql = new SQL() {
            {
                SELECT("MAX(v1.dimming_group_seq) AS dimming_group_seq");
                FROM("v_dimming_group v1");
            }
        };
        return sql.toString();
    }

    public String selectListSignageTemplateQry(Map<String, Object> paramMap) {
        SQL sql = new SQL() {
            {
                SELECT("t1.template_seq, t1.template_name, t1.template_explain, t1.template_content");
                FROM("t_signage_template t1");
                ORDER_BY("t1.template_seq");
            }};
        return sql.toString();
    }

    public String selectOneSignageLayoutQry(int tempateSeq) {
        SQL sql = new SQL() {
            {
                SELECT("t1.template_content");
                FROM("t_signage_template t1");
                WHERE("t1.template_seq::integer = " + tempateSeq);
            }
        };
        return sql.toString();
    }

    public String selectOneSignageLayoutUseQry() {
        SQL sql = new SQL() {
            {
                SELECT("t1.template_content");
                FROM("t_signage_template t1");
                WHERE("t1.use_yn = 'Y'");
            }
        };
        return sql.toString();
    }

    public String insertSignageTemplateQry(Map<String, Object> paramMap) {
        Map<String, Object> qryMap = SqlUtil.getInsertValuesStr(paramMap);

        SQL sql = new SQL() {{
            INSERT_INTO("t_signage_template");
            VALUES(qryMap.get("columns").toString(), qryMap.get("values").toString());
        }};
        return sql.toString();
    }

    public String updateSignageTemplateQry(Map<String, Object> paramMap) {
        String templateSeq = paramMap.get("templateSeq").toString();

        SQL sql = new SQL() {{
            UPDATE("t_signage_template");
            SET(SqlUtil.getMultiSetStr(paramMap));
            WHERE("template_seq =" + templateSeq);
        }};
        return sql.toString();
    }

    public String updateSignageLayoutQry(SignageRequestDto signageRequestDto) {
        String templateContent = signageRequestDto.getTemplateContent() != null ? signageRequestDto.getTemplateContent() : "";

        SQL sql = new SQL() {{
            UPDATE("t_signage_template");
            SET("template_content = '" + templateContent + "'");
            WHERE("template_seq = " + signageRequestDto.getTemplateSeq());
        }};
        return sql.toString();
    }

    public String updateSignageLayoutForGmQry(Map<String, Object> paramMap) {
        String templateContent = CommonUtil.validOneNull(paramMap, "templateContent");
        String useYn = CommonUtil.validOneNull(paramMap, "useYn");

        SQL sql = new SQL() {{
            UPDATE("t_signage_template");
            if(templateContent != null && !templateContent.equals("")) {
                SET("template_content = '" + templateContent + "'");
                SET("use_yn = '" + useYn + "'");
                WHERE("template_seq = " + paramMap.get("templateSeq"));
            } else {
                SET("use_yn = '" + useYn + "'");
            }
        }};
        return sql.toString();
    }

    public String deleteSignageTemplateQry(int seq) {
        SQL sql = new SQL() {{
            DELETE_FROM("t_signage_template");
            WHERE("template_seq =" + seq);
        }};
        return sql.toString();
    }

    public String selectListCctvHead(Map<String, Object> paramMap) { //헤더 카메라
        String administZone = CommonUtil.validOneNull(paramMap, "administZone");
        String facilitySeq = CommonUtil.validOneNull(paramMap, "facilitySeq");
        String longitude = CommonUtil.validOneNull(paramMap, "longitude");
        String latitude = CommonUtil.validOneNull(paramMap, "latitude");
        String view = CommonUtil.validOneNull(paramMap, "view");

        SQL sql = new SQL() {{ //레이어용
            SELECT("t1.*");
            FROM("t_facility t1");
            INNER_JOIN("v_facility_kind v1 on t1.facility_kind = v1.code_seq");
            INNER_JOIN("t_facility_opt t2 on t1.facility_seq = t2.facility_seq");
            WHERE( "v1.code_value = 'CCTV' " +
                    "and (t2.facility_opt_name = 'cctv_head' and t2.facility_opt_value = '1') " +
                    "and t1.station_seq is null " +
                    "and administ_zone like '" + administZone + "%' ");
            if(facilitySeq != ""){
                WHERE("t1.facility_seq = " + facilitySeq );
            }
//            if((latitude != "") && (longitude != "")){
//                WHERE("latitude = '" + latitude + "'" +
//                        " and longitude = '" + longitude + "'");
//            }
        }};
        if(view.equals("net")) { //투망감시
            SQL baseSql = new SQL() {{
                SELECT("t1.*," +
                        "ST_DISTANCE(" +
                            "(select st_geomfromtext(concat('point(', t1.longitude, ' ', t1.latitude, ')'),4326))::geography," +
                            "(select st_geomfromtext(concat('point(" + longitude + " " + latitude + ")'), 4326))::geography" +
                        ") as distance");
                FROM("t_facility t1");
                INNER_JOIN("v_facility_kind v1 on t1.facility_kind = v1.code_seq");
                INNER_JOIN("t_facility_opt tp on t1.facility_seq = tp.facility_seq");
                WHERE("v1.code_value = 'CCTV' " +
                        "and (tp.facility_opt_name = 'cctv_head' and tp.facility_opt_value = '1') " +
                        "and t1.station_seq is null " +
                        "and administ_zone like '" + administZone + "%' ");
            }};
            SQL rankSql = new SQL() {{
                SELECT("t2.*, row_number() over (order by t2.distance asc) as rnum");
                FROM(" ( " + baseSql.toString() + " ) t2 ");
                WHERE("0 < t2.distance and t2.distance < 500");
            }};
            sql = new SQL() {{
                SELECT("t3.*");
                FROM(" ( " + rankSql.toString() + " ) t3 ");
                WHERE("t3.rnum < 6");
            }};
        }
        return sql.toString();
    }

    public String selectListCctv(Map<String, Object> paramMap) { //같은 좌표
        String administZone = CommonUtil.validOneNull(paramMap, "administZone");
        String facilitySeq = CommonUtil.validOneNull(paramMap, "facilitySeq");
        String longitude = CommonUtil.validOneNull(paramMap, "longitude");
        String latitude = CommonUtil.validOneNull(paramMap, "latitude");
        String view = CommonUtil.validOneNull(paramMap, "view");

        SQL sql = new SQL() {{
            SELECT("t1.*");
            FROM("t_facility t1");
            INNER_JOIN("v_facility_kind v1 on t1.facility_kind = v1.code_seq");
            WHERE("v1.code_value = 'CCTV' " +
                    "and t1.station_seq is null " +
                    "and administ_zone like '" + administZone + "%'");
//            if(facilitySeq != ""){
//                WHERE("t1.facility_seq = " + facilitySeq );
//            }
            if(view.equals("group")) { //개소감시
                if((latitude != "") && (longitude != "")){
                    WHERE("latitude = '" + latitude + "'" +
                            " and longitude = '" + longitude + "'");
                }
            }
        }};
        return sql.toString();
    }
}
