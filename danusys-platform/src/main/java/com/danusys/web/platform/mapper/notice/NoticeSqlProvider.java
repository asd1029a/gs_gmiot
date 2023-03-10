package com.danusys.web.platform.mapper.notice;

import com.danusys.web.commons.app.CommonUtil;
import com.danusys.web.commons.app.SqlUtil;
import org.apache.ibatis.jdbc.SQL;

import java.util.Map;

public class NoticeSqlProvider {
    public String selectListQry(Map<String, Object> paramMap) {

        CommonUtil.validMapNull(paramMap);

        String keyword = paramMap.get("keyword").toString();
        String start = paramMap.get("start").toString();
        String length = paramMap.get("length").toString();
        String startDt = paramMap.get("startDt").toString();
        String endDt = paramMap.get("endDt").toString();

        SQL sql = new SQL() {{
            SELECT("t1.notice_seq" +
                            ", t1.notice_title" +
                            ", t1.notice_content" +
                            ", TO_CHAR(t1.insert_dt, 'YYYY-MM-DD HH24:MI:SS') AS insert_dt" +
                            ", t2.user_seq AS insert_user_id" +
                            ", TO_CHAR(t1.update_dt, 'YYYY-MM-DD HH24:MI:SS') AS update_dt" +
                            ", t3.user_seq AS update_user_id" +
                            ", t1.notice_file");
            FROM("t_notice t1");
            INNER_JOIN("t_user t2 on t1.insert_user_seq = t2.user_seq");
            LEFT_OUTER_JOIN("t_user t3 on t1.update_user_seq = t3.user_seq");
            if(!keyword.equals("")) {
                WHERE("notice_title LIKE '%" + keyword + "%'");
            }
            if(!startDt.equals("")) {
                WHERE("insert_dt >= to_timestamp('" + startDt + "', 'YYYY-MM-DD HH24:MI:SS')");
            }
            if(!endDt.equals("")) {
                WHERE("insert_dt <= to_timestamp('" + endDt + "', 'YYYY-MM-DD HH24:MI:SS')");
            }
            if (!start.equals("") && !length.equals("")) {
                LIMIT(length);
                OFFSET(start);
            }
        }};
        return sql.toString();
    }

    public String selectCountQry(Map<String, Object> paramMap) {

        CommonUtil.validMapNull(paramMap);

        String keyword = paramMap.get("keyword").toString();
        String startDt = paramMap.get("startDt").toString();
        String endDt = paramMap.get("endDt").toString();

        SQL sql = new SQL() {{
            SELECT("COUNT(*) AS count");
            FROM("t_notice t1");
            INNER_JOIN("t_user t2 on t1.insert_user_seq = t2.user_seq");
            LEFT_OUTER_JOIN("t_user t3 on t1.update_user_seq = t3.user_seq");
            if(!keyword.equals("")) {
                WHERE("notice_title LIKE '%" + keyword + "%'");
            }
            if(!startDt.equals("")) {
                WHERE("insert_dt >= to_timestamp('" + startDt + "', 'YYYY-MM-DD HH24:MI:SS')");
            }
            if(!endDt.equals("")) {
                WHERE("insert_dt <= to_timestamp('" + endDt + "', 'YYYY-MM-DD HH24:MI:SS')");
            }
        }};

        return sql.toString();
    }

    public String selectOneQry(Long noticeSeq) {
        SQL sql = new SQL() {{

            SELECT("t1.notice_seq" +
                    ", t1.notice_title" +
                    ", t1.notice_content" +
                    ", TO_CHAR(t1.insert_dt, 'YYYY-MM-DD HH24:MI:SS') AS insert_dt" +
                    ", t2.user_seq AS insert_user_id" +
                    ", TO_CHAR(t1.update_dt, 'YYYY-MM-DD HH24:MI:SS') AS update_dt" +
                    ", t3.user_seq AS update_user_id" +
                    ", t1.notice_file");
            FROM("t_notice t1");
            INNER_JOIN("t_user t2 on t1.insert_user_seq = t2.user_seq");
            LEFT_OUTER_JOIN("t_user t3 on t1.update_user_seq = t3.user_seq");
            WHERE("notice_seq =" + noticeSeq);
        }};
        return sql.toString();
    }

    public String selectKeyQry() {
        SQL sql = new SQL() {{
            SELECT("currval('t_notice_seq')");
        }};
        return sql.toString();
    }

    public String insertQry(Map<String, Object> paramMap) {
        Map<String, Object> qryMap = SqlUtil.getInsertValuesStr(paramMap);

        SQL sql = new SQL() {{
            INSERT_INTO("t_notice");
            VALUES(qryMap.get("columns").toString(), qryMap.get("values").toString());
        }};
        return sql.toString();
    }

    public String updateQry(Map<String, Object> paramMap) {
        System.out.println(paramMap);
        String noticeSeq = paramMap.get("noticeSeq").toString();

        SQL sql = new SQL() {{
            UPDATE("t_notice");
            SET(SqlUtil.getMultiSetStr(paramMap));
            WHERE("notice_seq =" + noticeSeq);
        }};
        return sql.toString();
    }

    public String deleteQry(Long noticeSeq) {
        SQL sql = new SQL() {{
            DELETE_FROM("t_notice");
            WHERE("notice_seq =" + noticeSeq);
        }};
        return sql.toString();
    }
}
