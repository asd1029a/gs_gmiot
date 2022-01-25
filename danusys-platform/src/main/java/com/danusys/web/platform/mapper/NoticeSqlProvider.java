package com.danusys.web.platform.mapper;

import org.apache.ibatis.jdbc.SQL;

import java.util.Map;

public class NoticeSqlProvider {
    public String selectList(Map<String, Object> paramMap) {
        String keyword = paramMap.get("keyword").toString();
        String start = paramMap.get("start").toString();
        String length = paramMap.get("length").toString();

        SQL sql = new SQL() {{

            SELECT("*");
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

    public String selectOne(int noticeSeq) {
        SQL sql = new SQL() {{

            SELECT("*");
            FROM("t_notice");
            WHERE("notice_seq =" + noticeSeq);
        }};
        return sql.toString();
    }

    public String insert(Map<String, Object> paramMap) {
        SQL sql = new SQL() {{

        }};
        return sql.toString();
    }

    public String update(Map<String, Object> paramMap) {
        SQL sql = new SQL() {{

        }};
        return sql.toString();
    }

    public String delete(int noticeSeq) {
        SQL sql = new SQL() {{

        }};
        return sql.toString();
    }
}
