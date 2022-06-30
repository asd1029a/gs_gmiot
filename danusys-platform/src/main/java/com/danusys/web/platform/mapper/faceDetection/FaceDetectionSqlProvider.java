package com.danusys.web.platform.mapper.faceDetection;

import com.danusys.web.commons.app.CommonUtil;
import com.danusys.web.commons.app.SqlUtil;
import org.apache.ibatis.jdbc.SQL;

import java.util.ArrayList;
import java.util.Map;

public class FaceDetectionSqlProvider {

    public String selectListQry(Map<String, Object> paramMap) {
        String keyword = CommonUtil.validOneNull(paramMap, "keyword");
        ArrayList faceKind = CommonUtil.valiArrNull(paramMap, "faceKind");
        SQL sql = new SQL() {{
            SELECT("*");
            FROM("t_face_detection");
            WHERE("face_status != 2");
            if (keyword != null && !keyword.equals("")){
                WHERE("face_name LIKE '%" + keyword + "%'");
            }
            if (faceKind != null && !faceKind.isEmpty()){
                WHERE("face_kind" + SqlUtil.getWhereInStr(faceKind));
            }
            ORDER_BY("face_seq");
        }};
        return sql.toString();
    }

    public String selectCountQry(Map<String, Object> paramMap) {
        String keyword = CommonUtil.validOneNull(paramMap, "keyword");
        ArrayList faceKind = CommonUtil.valiArrNull(paramMap, "faceKind");

        SQL sql = new SQL() {{
            SELECT("COUNT(*)");
            FROM("t_face_detection");
            WHERE("face_status != 2");
            if (keyword != null && !keyword.equals("")){
                WHERE("face_name LIKE '%" + keyword + "%'");
            }
            if (faceKind != null && !faceKind.isEmpty()){
                WHERE("face_kind" + SqlUtil.getWhereInStr(faceKind));
            }
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
            WHERE("face_status != 2");
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
