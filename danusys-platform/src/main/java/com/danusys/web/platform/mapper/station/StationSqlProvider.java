package com.danusys.web.platform.mapper.station;

import com.danusys.web.platform.util.SqlUtil;
import org.apache.ibatis.jdbc.SQL;

import java.util.Map;

public class StationSqlProvider {
    public String selectListQry(Map<String, Object> paramMap) {
        SQL sql = new SQL() {{

        }};
        return sql.toString();
    }

    public String selectOneQry(int seq) {
        SQL sql = new SQL() {{
        }};
        return sql.toString();
    }

    public String insertQry(Map<String, Object> paramMap) {
        Map<String, Object> qryMap = SqlUtil.getInsertValuesStr(paramMap);

        SQL sql = new SQL() {{
        }};
        return sql.toString();
    }

    public String updateQry(Map<String, Object> paramMap) {
        String noticeSeq = paramMap.get("noticeSeq").toString();

        SQL sql = new SQL() {{
        }};
        return sql.toString();
    }

    public String deleteQry(int seq) {
        SQL sql = new SQL() {{
        }};
        return sql.toString();
    }
}
