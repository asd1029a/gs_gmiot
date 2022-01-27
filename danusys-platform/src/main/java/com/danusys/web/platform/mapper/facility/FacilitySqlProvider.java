package com.danusys.web.platform.mapper.facility;

import com.danusys.web.platform.util.SqlUtil;
import org.apache.ibatis.jdbc.SQL;

import java.util.Map;

public class FacilitySqlProvider {

    public String selectList(Map<String, Object> paramMap) {
        SQL sql = new SQL() {{

        }};
        return sql.toString();
    }

    public String selectOne(int seq) {
        SQL sql = new SQL() {{
        }};
        return sql.toString();
    }

    public String insert(Map<String, Object> paramMap) {
        Map<String, Object> qryMap = SqlUtil.getInsertValuesStr(paramMap);

        SQL sql = new SQL() {{
        }};
        return sql.toString();
    }

    public String update(Map<String, Object> paramMap) {
        String noticeSeq = paramMap.get("noticeSeq").toString();

        SQL sql = new SQL() {{
        }};
        return sql.toString();
    }

    public String delete(int seq) {
        SQL sql = new SQL() {{
        }};
        return sql.toString();
    }
}
