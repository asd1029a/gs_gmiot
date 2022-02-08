package com.danusys.web.platform.mapper.notice;

import com.danusys.web.platform.util.SqlUtil;
import org.apache.ibatis.jdbc.SQL;

import java.util.Map;

public class NoticeSqlProvider {

    public String selectListQry(Map<String, Object> paramMap) {
        String keyword = paramMap.get("keyword").toString();
        String start = paramMap.get("start").toString();
        String length = paramMap.get("length").toString();

        SQL sql = new SQL() {{

            SELECT("notice_seq" +
                            ",notice_title" +
                            ",notice_content" +
                            ", TO_CHAR(insert_dt, 'YYYY-DD-MM HH:MI:SS') AS insert_dt" +
                            ",insert_user_seq" +
                            ", TO_CHAR(update_dt, 'YYYY-DD-MM HH:MI:SS') AS update_dt" +
                            ",update_user_seq" +
                            ",notice_file");
            FROM("t_notice");
            if(keyword != null && !keyword.equals("")) {
                WHERE("notice_title LIKE" + keyword);
            }
            if (!start.equals("") && !length.equals("")) {
                LIMIT(length);
                OFFSET(start);
            }
        }};
        return sql.toString();
    }

    public String selectOneQry(int noticeSeq) {
        SQL sql = new SQL() {{

            SELECT("notice_seq" +
                    ",notice_title" +
                    ",notice_content" +
                    ", TO_CHAR(insert_dt, 'YYYY-DD-MM HH:MI:SS') AS insert_dt" +
                    ",insert_user_seq" +
                    ", TO_CHAR(update_dt, 'YYYY-DD-MM HH:MI:SS') AS update_dt" +
                    ",update_user_seq" +
                    ",notice_file");
            FROM("t_notice");
            WHERE("notice_seq =" + noticeSeq);
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
        String noticeSeq = paramMap.get("noticeSeq").toString();

        SQL sql = new SQL() {{
            UPDATE("t_notice");
            SET(SqlUtil.getMultiSetStr(paramMap));
            WHERE("notice_seq =" + noticeSeq);
        }};
        return sql.toString();
    }

    public String deleteQry(int noticeSeq) {
        SQL sql = new SQL() {{
            DELETE_FROM("t_notice");
            WHERE("notice_seq =" + noticeSeq);
        }};
        return sql.toString();
    }
}
