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
            WHERE("use_kind != 'D'");
            if (!start.equals("") && !length.equals("")) {
                LIMIT(length);
                OFFSET(start);
            };
        }};
        return sql.toString();
    }

    public String selectCountCodeQry(Map<String, Object> paramMap) {
        String keyword = paramMap.get("keyword").toString();
        int pParentCode = (int) paramMap.get("pParentCode");

        SQL sql = new SQL() {{
            SELECT("COUNT(*) AS count");
            FROM("t_common_code");
            WHERE("parent_code_seq = "+pParentCode);
        }};
        return sql.toString();
    }

    public String selectListTypeQry(Map<String, Object> paramMap) {
        SQL sql = new SQL() {{
            SELECT("*");
            switch (paramMap.get("type").toString()) {
                case "stationKind" : FROM("v_facility_station"); break;
                case "district" : FROM("v_facility_district"); break;
                case "facilityKind" : FROM("v_facility_kind"); break;
                case "eventKind" : FROM("v_event_kind"); break;
                case "administZone" : FROM("v_administ"); break;
                default : FROM("t_common_code"); break;
            }
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
