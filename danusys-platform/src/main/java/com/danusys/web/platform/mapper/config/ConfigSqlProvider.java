package com.danusys.web.platform.mapper.config;

import com.danusys.web.commons.app.CommonUtil;
import com.danusys.web.commons.app.StrUtils;
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
            if(keyword != null && !keyword.equals("")) {
                WHERE("code_name LIKE '%" + keyword + "%'");
            }
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
            if(keyword != null && !keyword.equals("")) {
                WHERE("code_name LIKE '%" + keyword + "%'");
            }
        }};
        return sql.toString();
    }

    public String selectListTypeQry(Map<String, Object> paramMap) {
        SQL sql = new SQL() {{
            SELECT("*");
            switch (CommonUtil.validOneNull(paramMap, "type")) {
                case "stationKind" : FROM("v_facility_station"); break;
                case "district" : FROM("v_facility_district"); break;
                case "facilityKind" : FROM("v_facility_kind"); break;
                case "eventKind" : {
                    FROM("(SELECT t1.*" +
                            "from v_event_kind t1" +
                            ", (" +
                            "    SELECT *" +
                            "    FROM v_event_kind" +
                            "    WHERE level = 1 AND code_value = '" + CommonUtil.validOneNull(paramMap, "subType") + "'" +
                            ") t2" +
                            " WHERE t1.code_value = '" + CommonUtil.validOneNull(paramMap, "subType") + "' OR t2.code_seq = t1.parent_code_seq OR t1.level = 0" +
                            " ORDER BY level, code_seq) t");
                    break;
                }
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
