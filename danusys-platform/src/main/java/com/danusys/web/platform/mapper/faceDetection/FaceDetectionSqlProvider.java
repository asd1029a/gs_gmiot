package com.danusys.web.platform.mapper.faceDetection;

import com.danusys.web.commons.app.CommonUtil;
import com.danusys.web.commons.app.SqlUtil;
import org.apache.ibatis.jdbc.SQL;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FaceDetectionSqlProvider {

    public String selectListQry(Map<String, Object> paramMap) {
        String keyword = CommonUtil.validOneNull(paramMap, "keyword");

        SQL sql = new SQL() {{
            SELECT("*");
            FROM("t_face_detection");
            WHERE("face_status != 2");
        }};
        return sql.toString();
    }

    public String selectCountQry(Map<String, Object> paramMap) {
        String keyword = CommonUtil.validOneNull(paramMap, "keyword");

        SQL sql = new SQL() {{
            SELECT("COUNT(*)");
            FROM("t_face_detection");
            WHERE("face_status != 2");
        }};
        return sql.toString();
    }

    public String selectOneQry(int seq) {
        SQL sql = new SQL() {{
            SELECT("*");
            FROM("t_face_detection");
            WHERE("face_seq = " + seq);
            WHERE("face_status != 2");
        }};
        return sql.toString();
    }

    public String selectOneNameQry(String name) {
        SQL sql = new SQL() {{
            SELECT("COUNT(*) AS count");
            FROM("t_face_detection");
            WHERE("face_name = '" + name + "'");
        }};
        return sql.toString();
    }

    public String insertQry(Map<String, Object> paramMap) {
        Map<String, Object> qryMap = SqlUtil.getInsertValuesStr(paramMap);

        SQL sql = new SQL() {{
            INSERT_INTO("t_face_detection");
            VALUES(qryMap.get("columns").toString(), qryMap.get("values").toString());
        }};
        return sql.toString();
    }

    public String updateQry(Map<String, Object> paramMap) {
        String faceSeq =  CommonUtil.validOneNull(paramMap, "faceSeq");
        SQL sql = new SQL() {{
            UPDATE("t_face_detection");
            SET(SqlUtil.getMultiSetStr(paramMap));
            WHERE("face_seq =" + faceSeq);
        }};
        return sql.toString();
    }

    public String delete(int seq) {
        SQL sql = new SQL() {{
            UPDATE("t_face_detection");
            SET("face_status = 2");
            WHERE("face_seq = " + seq);
        }};
        return sql.toString();
    }
}
