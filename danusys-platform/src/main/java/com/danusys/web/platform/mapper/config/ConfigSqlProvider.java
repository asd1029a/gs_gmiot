package com.danusys.web.platform.mapper.config;

import org.apache.ibatis.jdbc.SQL;

import java.util.Map;

public class ConfigSqlProvider {
    public String selectAllCode(Map<String, Object> paramMap) {
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

    public String selectOneCode(int seq) {
        SQL sql = new SQL() {{

            SELECT("*");
            FROM("t_common_code");
            WHERE("code_seq =" + seq);
        }};
        return sql.toString();
    }
}
