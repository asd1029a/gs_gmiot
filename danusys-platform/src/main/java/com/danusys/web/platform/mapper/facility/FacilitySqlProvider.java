package com.danusys.web.platform.mapper.facility;

import com.danusys.web.commons.app.CommonUtil;
import com.danusys.web.commons.app.SqlUtil;
import org.apache.ibatis.jdbc.SQL;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FacilitySqlProvider {

    public String selectListQry(Map<String, Object> paramMap) {
        String keyword = CommonUtil.validOneNull(paramMap,"keyword");
        String facilityKind = CommonUtil.validOneNull(paramMap,"facilityKind");
        String start = CommonUtil.validOneNull(paramMap,"start");
        String length = CommonUtil.validOneNull(paramMap,"length");
        String createType = CommonUtil.validOneNull(paramMap,"createType");

        SQL sql = new SQL() {{
            SELECT("t1.facility_seq, t1.facility_id" +
                    ", t1.administ_zone, t1.facility_image" +
                    ", t1.facility_instl_info, t1.facility_instl_dt" +
                    ", t1.facility_status, t1.latitude" +
                    ", t1.longitude, t1.insert_dt, t3.id AS insert_user_id" +
                    ", t1.update_user_seq , t4.id AS update_user_id" +
                    ", t2.code_value AS facility_kind" +
                    ", t5.station_kind, t5.station_name, t5.address");
            FROM("t_facility t1");
            INNER_JOIN("v_facility_kind t2 on t1.facility_kind = t2.code_seq");
            LEFT_OUTER_JOIN("t_user t3 on t1.insert_user_seq = t3.user_seq");
            LEFT_OUTER_JOIN("t_user t4 on t1.update_user_seq = t4.user_seq");
            LEFT_OUTER_JOIN("t_station t5 on t1.station_seq = t5.station_seq");
            if(facilityKind != null && !facilityKind.equals("")) {
                WHERE("t2.code_value = '" + facilityKind + "'");
                if("lamp_road".equals(facilityKind)) {
                    String modifyQry = "";
                        if("mod".equals(createType)) {
                            modifyQry = "AND v1.dimming_group_seq::integer != " + paramMap.get("dimmingGroupSeq");
                        }
                        WHERE("NOT EXISTS (" +
                                "SELECT * " +
                                "FROM v_dimming_group v1 " +
                                "WHERE v1.facility_seq = t1.facility_seq " +
                                modifyQry +
                                ")");
                }
            }
            if(keyword != null && !keyword.equals("")) {
                WHERE("t1.facility_id LIKE" + keyword);
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
        String keyword = paramMap.get("keyword").toString();

        SQL sql = new SQL() {{
            SELECT("COUNT(*) AS count");
            FROM("t_facility t1");
            INNER_JOIN("t_facility_opt t2 on t1.facility_seq = t2.facility_seq");
            if(keyword != null && !keyword.equals("")) {
                WHERE("facility_kind LIKE" + keyword);
            }
        }};
        return sql.toString();
    }

    public String selectOneQry(int seq) {
        SQL sql = new SQL() {{
            SELECT("*");
            FROM("t_facility t1");
            INNER_JOIN("t_facility_opt t2 on t1.facility_seq = t2.facility");
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
            if( index == 0 ) {
                sb.append("INSERT INTO t_facility_opt (")
                        .append(SqlUtil.getInsertValuesStr(facilityOpt).get("columns").toString())
                        .append(")")
                        .append(" VALUES ");
            }
            sb.append("(")
                    .append(SqlUtil.getInsertValuesStr(facilityOpt).get("values").toString());
            if( index == facilityOptList.size() -1 ) {
                sb.append(")");
                break;
            } else {
                sb.append("), ");
            }
            index ++;
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
        String facilityOptType = CommonUtil.validOneNull(paramMap,"facilityOptType");
        List<Integer> delFacilitySeqList = (List<Integer>) paramMap.get("delFacilitySeqList");
        List<String> ignoreDeleteOptList = (List<String>) paramMap.get("ignoreDeleteOptList");
        String ignoreDeleteOptListStr = "";
        String delFacilitySeqListStr = delFacilitySeqList
                .stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));

        StringBuilder sb = new StringBuilder();
        int index = 0;
        if(ignoreDeleteOptList != null && !ignoreDeleteOptList.isEmpty()) {
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

        SQL sql = new SQL() {{
            DELETE_FROM("t_facility_opt t1 ");
            WHERE("t1.facility_seq IN ( " + delFacilitySeqListStr +" )");
            if(ignoreDeleteOptList != null && !ignoreDeleteOptList.isEmpty()) {
                WHERE("t1.facility_opt_name NOT IN ( " + sb.toString() + " )");
            }
            WHERE("t1.facility_opt_type::varchar = (" +
                    "SELECT code_value " +
                    "FROM v_facility_opt_type " +
                    "WHERE code_id = '" + facilityOptType + "')");
        }};
        return sql.toString();
    }

    public String selectListDimmingGroupQry(Map<String, Object> paramMap) {
        String keyword = CommonUtil.validOneNull(paramMap,"keyword");
        String start = CommonUtil.validOneNull(paramMap,"start");
        String length = CommonUtil.validOneNull(paramMap,"length");

        SQL sql = new SQL() {{

            SELECT("v1.dimming_group_seq, v1.dimming_group_name");
            FROM("(" +
                    "SELECT *" +
                    "FROM v_dimming_group" +
                    ") v1");
            if(keyword != null && !keyword.equals("")) {
                WHERE("v1.dimming_group_name LIKE" + keyword);
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
        String keyword = CommonUtil.validOneNull(paramMap,"keyword");

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
            if(keyword != null && !keyword.equals("")) {
                WHERE("v1.dimming_group_name LIKE" + keyword);
            }
            GROUP_BY("v1.dimming_group_name ) s1");
        }};
        return sql.toString();
    }

    public String selectListLampRoadInDimmingGroupQry(Map<String, Object> paramMap) {
        String dimmingGroupSeq = CommonUtil.validOneNull(paramMap,"dimmingGroupSeq");

        SQL sql = new SQL() {
            {
                SELECT("t1.facility_seq, t1.facility_Id, t1.longitude, t1.latitude, t1.administ_zone" +
                        ", v1.dimming_group_name, v1.dimming_group_seq, v1.keep_bright_time" +
                        ", v1.max_bright_time, v1.min_bright_time, v1.dimming_time_zone");
                FROM("t_facility t1");
                INNER_JOIN("v_dimming_group v1 on t1.facility_seq = v1.facility_seq");
                WHERE("v1.dimming_group_seq::integer = " + dimmingGroupSeq);
            }};
        return sql.toString();
    }

    public String selectOneLastDimmingGroupSeqQry() {
        SQL sql = new SQL() {
            {
               SELECT("MAX(v1.dimming_group_seq) AS dimming_group_seq");
               FROM("v_dimming_group v1");
            }};
        return sql.toString();
    }

    public String selectListSignageTemplateQry(Map<String, Object> paramMap) {
        //String signageTemplateSeq = CommonUtil.validOneNull(paramMap, "templateSeq");

        SQL sql = new SQL() {
            {
                SELECT("t1.template_seq, t1.template_name, t1.template_explain, t1.template_content");
                FROM("t_signage_template t1");
                //WHERE("t1.template_seq::integer = " + signageTemplateSeq);
            }};
        return sql.toString();
    }
}
