package com.danusys.web.commons.auth.mapper.account;

import com.danusys.web.commons.app.SqlUtil;
import org.apache.ibatis.jdbc.SQL;

import java.util.Map;

public class AccountSqlProvider {
    public String selectListUserQry(Map<String, Object> paramMap) {
        String keyword = paramMap.get("keyword").toString();
        String start = paramMap.get("start").toString();
        String length = paramMap.get("length").toString();

        SQL sql = new SQL() {{
            SELECT("user_seq, id, email, tel, last_login_dt" +
                    ", status, update_user_seq, insert_dt, user_name");
            FROM("t_user");
            if (!keyword.equals("")){
                WHERE("user_name = '%" + keyword + "%'");
            }
            if (!start.equals("") && !length.equals("")) {
                LIMIT(length);
                OFFSET(start);
            };
        }};
        return sql.toString();
    }

    public String selectCountUserQry(Map<String, Object> paramMap) {
        String keyword = paramMap.get("keyword").toString();

        SQL sql = new SQL() {{
            SELECT("COUNT(*) AS count");
            FROM("t_user");
        }};
        return sql.toString();
    }


    public String selectOneUserQry(int seq) {
        SQL sql = new SQL() {{

            SELECT("user_seq, id, email, tel, status, user_name");
            FROM("t_user");
            WHERE("user_seq =" + seq);
        }};
        return sql.toString();
    }

    public String insertUserQry(Map<String, Object> paramMap) {
        Map<String, Object> qryMap = SqlUtil.getInsertValuesStr(paramMap);

        SQL sql = new SQL() {{
            INSERT_INTO("t_user");
            VALUES(qryMap.get("columns").toString(), qryMap.get("values").toString());
        }};
        return sql.toString();
    }


    public String updateUserQry(Map<String, Object> paramMap) {
        System.out.println(paramMap);
        String userSeq = paramMap.get("userSeq").toString();

        SQL sql = new SQL() {{
            UPDATE("t_user");
            SET(SqlUtil.getMultiSetStr(paramMap));
            WHERE("user_seq =" + userSeq);
        }};
        return sql.toString();
    }
}