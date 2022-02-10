package com.danusys.web.platform.mapper.config;

import org.apache.ibatis.jdbc.SQL;

import java.util.Map;

public class ConfigSqlProvider {
    public String selectListCodeQry(Map<String, Object> paramMap) {
        String keyword = paramMap.get("keyword").toString();
        String start = paramMap.get("start").toString();
        String length = paramMap.get("length").toString();
        int pParentCode = (int) paramMap.get("pParentCode");

        SQL sql = new SQL() {{
            SELECT("*");
            FROM("t_common_code");
            WHERE("parent_code_seq = "+pParentCode);
            if (!start.equals("") && !length.equals("")) {
                LIMIT(length);
                OFFSET(start);
            };
        }};
        return sql.toString();
    }

    public String selectListViewStationQry(Map<String, Object> paramMap) {
        SQL sql = new SQL() {{
            SELECT("*");
            FROM("v_facility_station");
        }};
        return sql.toString();
    }

    public String selectListViewDirectionQry(Map<String, Object> paramMap) {
        SQL sql = new SQL() {{
            SELECT("*");
            FROM("v_facility_direction");
        }};
        return sql.toString();
    }

    public String selectListViewDistrictQry(Map<String, Object> paramMap) {
        SQL sql = new SQL() {{
            SELECT("*");
            FROM("v_facility_district");
        }};
        return sql.toString();
    }

    public String selectListViewKindQry(Map<String, Object> paramMap) {
        SQL sql = new SQL() {{
            SELECT("*");
            FROM("v_facility_kind");
        }};
        return sql.toString();
    }

    public String selectListViewProblemQry(Map<String, Object> paramMap) {
        SQL sql = new SQL() {{
            SELECT("*");
            FROM("v_facility_problem");
        }};
        return sql.toString();
    }

    public String selectOneCodeQry(int seq) {
        SQL sql = new SQL() {{

            SELECT("*");
            FROM("t_common_code");
            WHERE("code_seq =" + seq);
        }};
        return sql.toString();
    }

}
