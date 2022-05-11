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
        }};
        return sql.toString();
    }

    public String selectCountQry(Map<String, Object> paramMap) {
        String keyword = CommonUtil.validOneNull(paramMap, "keyword");

        SQL sql = new SQL() {{
            SELECT("COUNT(*)");
            FROM("t_face_detection");
        }};
        return sql.toString();
    }

    public String selectOneQry(int seq) {
        SQL sql = new SQL() {{
            SELECT("*");
            FROM("t_face_detection");
            WHERE("face_seq = " + seq);
        }};
        return sql.toString();
    }
}
