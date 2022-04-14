package com.danusys.web.platform.mapper.facility;

import com.danusys.web.commons.app.CommonUtil;
import com.danusys.web.commons.app.SqlUtil;
import org.apache.ibatis.jdbc.SQL;

import java.util.Map;

public class FacilitySqlProvider {

    public String selectListQry(Map<String, Object> paramMap) {
        String keyword = CommonUtil.validOneNull(paramMap,"keyword");
        String start = CommonUtil.validOneNull(paramMap,"start");
        String length = CommonUtil.validOneNull(paramMap,"length");
        String optType = CommonUtil.validOneNull(paramMap,"optType");
        String dimmingGroupSeq = CommonUtil.validOneNull(paramMap,"dimmingGroupSeq");

        SQL sql = new SQL() {{
            SELECT("*, '' as station_kind, '' as station_name, '' as address");
            FROM("t_facility t1");
            if("dimming".equals(optType)) {
                INNER_JOIN("v_dimming_group v1 on t1.facility_seq = v1.facility_seq");
                if(dimmingGroupSeq != null) {
                    WHERE("v1.dimming_group_seq::integer = " + dimmingGroupSeq);
                }
            }
            if(keyword != null && !keyword.equals("")) {
                WHERE("facility_kind LIKE" + keyword);
            }
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
        Map<String, Object> qryMap = SqlUtil.getInsertValuesStr(paramMap);

        SQL sql = new SQL() {{
            INSERT_INTO("t_facility_opt");
            VALUES(qryMap.get("columns").toString(), qryMap.get("values").toString());
        }};
        return sql.toString();
    }

    public String updateQry(Map<String, Object> paramMap) {
        String facilitySeq = paramMap.get("facilitySeq").toString();

        SQL sql = new SQL() {{
            SQL sql = new SQL() {{
                UPDATE("t_facility");
                SET(SqlUtil.getMultiSetStr(paramMap));
                WHERE("facility_seq =" + facilitySeq);
            }};
        }};
        return sql.toString();
    }

    public String updateOptQry(Map<String, Object> paramMap) {
        String facilitySeq = paramMap.get("facilitySeq").toString();

        SQL sql = new SQL() {{
            SQL sql = new SQL() {{
                UPDATE("t_facility_opt");
                SET(SqlUtil.getMultiSetStr(paramMap));
                WHERE("facility_seq =" + facilitySeq);
            }};
        }};
        return sql.toString();
    }

    public String deleteQry(int seq) {
        SQL sql = new SQL() {{
            SQL sql = new SQL() {{
               DELETE_FROM("t_facility");
               WHERE("facility_seq =" + seq);
            }};
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
}
