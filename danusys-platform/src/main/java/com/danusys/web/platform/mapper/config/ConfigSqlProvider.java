package com.danusys.web.platform.mapper.config;

import com.danusys.web.commons.app.CommonUtil;
import com.danusys.web.commons.app.SqlUtil;
import com.danusys.web.commons.app.StrUtils;
import org.apache.ibatis.jdbc.SQL;

import java.util.Map;

public class ConfigSqlProvider {
    public String selectListCodeQry(Map<String, Object> paramMap) {
        String keyword = paramMap.get("keyword").toString();
        String start = paramMap.get("start").toString();
        String length = paramMap.get("length").toString();
        int parentCodeSeq = (int) paramMap.get("parentCodeSeq");

        SQL sql = new SQL() {{
            SELECT("code_seq, code_id, code_name, code_value" +
                    ", parent_code_seq, use_kind, insert_user_seq, update_user_seq" +
                    ", to_char(insert_dt, 'YYYY-MM-DD HH24:MI:SS') AS insert_dt" +
                    ", to_char(update_dt, 'YYYY-MM-DD HH24:MI:SS') AS update_dt");
            FROM("t_common_code");
            WHERE("parent_code_seq = " + parentCodeSeq);
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
        int parentCodeSeq = (int) paramMap.get("parentCodeSeq");

        SQL sql = new SQL() {{
            SELECT("COUNT(*) AS count");
            FROM("t_common_code");
            WHERE("parent_code_seq = " + parentCodeSeq);
            WHERE("use_kind != 'D'");
            if(keyword != null && !keyword.equals("")) {
                WHERE("code_name LIKE '%" + keyword + "%'");
            }
        }};
        return sql.toString();
    }

    public String insertCodeQry(Map<String, Object> paramMap) {
        Map<String, Object> qryMap = SqlUtil.getInsertValuesStr(paramMap);

        SQL sql = new SQL() {{
            INSERT_INTO("t_common_code");
            VALUES(qryMap.get("columns").toString(), qryMap.get("values").toString());
        }};
        return sql.toString();
    }

    public String updateCodeQry(Map<String, Object> paramMap) {
        String codeSeq = CommonUtil.validOneNull(paramMap, "codeSeq");

        SQL sql = new SQL() {{
            UPDATE("t_common_code");
            SET(SqlUtil.getMultiSetStr(paramMap));
            WHERE("code_seq =" + codeSeq);
        }};
        return sql.toString();
    }

    public String deleteCodeQry(int codeSeq) {
        SQL sql = new SQL() {{
            DELETE_FROM("t_common_code");
            WHERE("code_seq =" + codeSeq);
            OR();
            WHERE("parent_code_seq = " + codeSeq);
        }};
        return sql.toString();
    }

    public String selectListTypeQry(Map<String, Object> paramMap) {
        SQL sql = new SQL() {{
            String subType = CommonUtil.validOneNull(paramMap, "subType");
            String ignoreType = CommonUtil.validOneNull(paramMap, "ignoreType");

            SELECT("*");
            switch (CommonUtil.validOneNull(paramMap, "type")) {
                case "stationKind" : FROM("v_station_kind"); break;
                case "district" : FROM("v_facility_district"); break;
                case "facilityKind" : FROM("v_facility_kind"); break;
                case "eventKind" : {
                    FROM("(SELECT t1.* " +
                            "from v_event_kind t1" +
                            ", (" +
                            "    SELECT *" +
                            "    FROM v_event_kind" +
                            "    WHERE level = 1 " +
                            "    AND code_value = '" + subType + "'" +
                            ") t2" +
                            " WHERE t2.code_seq = t1.parent_code_seq " +
                            "   AND t1.code_value != '" + ignoreType + "'" +
                            "   OR t1.level = 0" +
                            " ORDER BY level, code_seq) t");
                    break;
                }
                case "administZone" : {
                    FROM("v_administ");
                    WHERE("substr(code_value, 1, 5) = '" + subType + "'");
                    OR();
                    WHERE("level = 0");
                } break;
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

    public String selectOneEventKindQry(String kind) {
        SQL sql = new SQL() {{

            SELECT("*");
            FROM("t_common_code");
            WHERE("code_value = '" + kind + "'");
        }};
        return sql.toString();
    }

    public String selectListInitMntrParam(String pageTypeCodeValue) {
        SQL sql = new SQL() {{
            SELECT("p.code_seq, p.code_id, p.code_name, p.code_value, c.code_value AS child_value, c.code_seq AS child_seq, c.code_name AS child_name ");
            FROM("t_common_code p ");
            INNER_JOIN("t_common_code c on c.parent_code_seq = p.code_seq ");
            WHERE("p.parent_code_seq = (select code_seq from t_common_code where code_id = 'mntr_page_type' and code_value = '" + pageTypeCodeValue + "')");
        }};
        return sql.toString();
    }

    public String selectOneVideoNetInfoQry(String ipClassAB) {
        SQL sql = new SQL() {{
            SELECT("net_mapping_ip, seq, ip, port, insert_date, update_date");
            FROM("t_net_mapping_sub");
            WHERE("seq = (select seq from t_net_mapping where ip = '" + ipClassAB + "')");
        }};
        return sql.toString();
    }

    public String selectOneVideoConfigQry() {
        SQL sql = new SQL() {{
            SELECT("config_seq, name, value, type, insert_dt, update_dt");
            FROM("t_config");
        }};
        return sql.toString();
    }
}
