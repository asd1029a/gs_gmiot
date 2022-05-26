package com.danusys.web.platform.mapper.statistics;

import org.apache.ibatis.jdbc.SQL;
import org.springframework.beans.factory.annotation.Value;

import java.util.Map;

public class StatisticsSqlProvider {

    public String selectSumQry(Map<String, Object> paramMap) {
        return null;
    }

    public String selectAvgQry(Map<String, Object> paramMap) {
        return null;
    }

    public String selectMapQry(Map<String, Object> paramMap) {
        return null;
    }

    public String selectGeoJsonQry(String emdCode) {
        SQL sql = new SQL() {{
            SELECT("col_adm_se, emd_cd, emd_nm AS name, ST_ASGeoJSON(geom) AS coordinates");
            FROM("t_area_emd");
            WHERE("col_adm_se IN ('" +  emdCode + "')");
        }};
        return sql.toString();
    }

}
