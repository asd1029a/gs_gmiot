package com.danusys.web.commons.auth.mapper.account;

import com.danusys.web.commons.app.CommonUtil;
import com.danusys.web.commons.app.SqlUtil;
import org.apache.ibatis.jdbc.SQL;


import java.util.ArrayList;
import java.util.Map;

public class AccountSqlProvider {

    /* 사용자 CRUD */
    public String selectListUserQry(Map<String, Object> paramMap) {
        String keyword = paramMap.get("keyword").toString();

        String status = String.join("', '", CommonUtil.valiArrNull(paramMap,"status"));

        String start = paramMap.get("start").toString();
        String length = paramMap.get("length").toString();

        SQL sql = new SQL() {{
            SELECT("t1.user_seq, t1.id, t1.email, t1.tel, TO_CHAR(t1.last_login_dt, 'YYYY-MM-DD HH24:MI:SS') AS last_login_dt" +
                    ", t1.status, t1.update_user_seq, TO_CHAR(t1.insert_dt, 'YYYY-MM-DD HH24:MI:SS') AS insert_dt, t1.user_name" +
                    ", t2.code_seq, t2.code_id, t2.code_value, t2.code_name");
            FROM("t_user t1");
            JOIN("v_user_status t2 on t1.status = t2.code_value");
            if (!keyword.equals("")){
                WHERE("user_name = '%" + keyword + "%'");
            }
            if(!status.equals("")){
                WHERE("status in ('" + status + "')");
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

        //ArrayList status = CommonUtil.valiArrNull(paramMap,"status");
        String status = String.join("', '", CommonUtil.valiArrNull(paramMap,"status"));

        SQL sql = new SQL() {{
            SELECT("COUNT(*) AS count");
            FROM("t_user");
            if (!keyword.equals("")){
                WHERE("user_name = '%" + keyword + "%'");
            }
            if(!status.equals("")){
                WHERE("status in ('" + status + "')");
            }
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

    /* 사용자 그룹 CRUD */
    public String selectListUserGroupQry(Map<String, Object> paramMap) {
        String keyword = paramMap.get("keyword").toString();
        String start = paramMap.get("start").toString();
        String length = paramMap.get("length").toString();

        SQL sql = new SQL() {{
            SELECT("S1.user_group_seq" +
                    ", S1.user_group_name" +
                    ", S1.user_group_remark" +
                    ", TO_CHAR(S1.insert_dt, 'YYYY-MM-DD HH24:MI:SS') AS insert_dt" +
                    ", TO_CHAR(S1.update_dt, 'YYYY-MM-DD HH24:MI:SS') AS update_dt" +
                    ", CASE WHEN (S2.user_cnt=0 OR S2.user_cnt IS NULL)" +
                    " THEN '-' WHEN S2.user_cnt=1" +
                    " THEN S3.user_name" +
                    " ELSE CONCAT(S3.user_name,' 외 ',(S2.user_cnt-1),'명')" +
                    " END user_name");
            FROM(" t_user_group AS S1");
            LEFT_OUTER_JOIN("(" +
                        "SELECT user_group_seq, COUNT(user_seq) AS user_cnt, MIN(user_seq) AS user_seq"+
                        " FROM t_user_group_in_user"+
                        " GROUP BY user_group_seq" +
                        ") AS S2 ON S1.user_group_seq = S2.user_group_seq");
            LEFT_OUTER_JOIN("t_user S3 ON S2.user_seq = S3.user_seq");
            if (!keyword.equals("")){
                WHERE("user_group_name = '%" + keyword + "%'");
            }
            if (!start.equals("") && !length.equals("")) {
                LIMIT(length);
                OFFSET(start);
            };
        }};
        return sql.toString();
    }

    public String selectCountUserGroupQry(Map<String, Object> paramMap) {
        String keyword = paramMap.get("keyword").toString();

        SQL sql = new SQL() {{
            SELECT("COUNT(*) AS count");
            FROM("t_user_group");
        }};
        return sql.toString();
    }

    public String selectOneUserGroupQry(int seq) {
        SQL sql = new SQL() {{
            SELECT("user_group_seq, user_group_name, user_group_remark, TO_CHAR(insert_dt, 'YYYY-MM-DD HH24:MI:SS') AS insert_dt" +
                    ", insert_user_seq, TO_CHAR(update_dt, 'YYYY-MM-DD HH24:MI:SS') AS update_dt, update_user_seq, user_group_status");
            FROM("t_user_group");
            WHERE("user_group_seq =" + seq);
        }};
        return sql.toString();
    }
}