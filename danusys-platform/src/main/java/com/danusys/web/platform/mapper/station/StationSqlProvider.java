package com.danusys.web.platform.mapper.station;

import com.danusys.web.commons.app.CommonUtil;
import com.danusys.web.commons.app.SqlUtil;
import org.apache.ibatis.jdbc.SQL;

import java.util.ArrayList;
import java.util.Map;

public class StationSqlProvider {

    public String selectListQry(Map<String, Object> paramMap) {
        String keyword = CommonUtil.validOneNull(paramMap, "keyword");
        ArrayList<String> station = CommonUtil.valiArrNull(paramMap, "station");
        ArrayList<String> administZone = CommonUtil.valiArrNull(paramMap, "administZone");
        ArrayList<String> facilityKind = CommonUtil.valiArrNull(paramMap, "facilityKind");
        String start = CommonUtil.validOneNull(paramMap, "start");
        String length = CommonUtil.validOneNull(paramMap, "length");

        SQL sql = new SQL() {{
            SELECT("t1.station_seq, t1.station_name, t1.station_kind, t1.administ_zone" +
                    ", t1.station_image, t1.station_compet_dt, t1.station_size" +
                    ", t1.station_material, t1.latitude, t1.longitude" +
                    ", t2.code_name  AS station_kind_name" +
                    ", t2.code_value AS station_kind_value" +
                    ", t3.emd_nm     AS administ_zone_name");
            FROM("t_station t1");
            LEFT_OUTER_JOIN("v_facility_station t2 on t1.station_kind = t2.code_seq");
//            현재 뷰테이블과 시설물 구역이 맞지 않아 임시로 조회
//            LEFT_OUTER_JOIN("v_administ t3 on t1.administ_zone = t3.code_value");
            LEFT_OUTER_JOIN("t_area_emd t3 on t1.administ_zone = t3.emd_cd");

            if (facilityKind != null && !facilityKind.isEmpty()) {
                SQL innerSql = new SQL() {{
                    SELECT("station_seq" +
                            ", CASE WHEN cnt = 1 THEN in_facility_kind_name" +
                            " ELSE CONCAT(in_facility_kind_name, ' 외 ', CAST(cnt - 1 AS VARCHAR(20)), '종')" +
                            " END AS in_facility_kind");
                    FROM("(" +
                            "    SELECT t.station_seq" +
                            "      , MIN(t.facility_kind_name) AS in_facility_kind_name" +
                            "      , COUNT(*)                  AS cnt" +
                            "    FROM (" +
                            "      SELECT t1.station_seq" +
                            "           , t1.facility_kind" +
                            "           , t2.code_name  AS facility_kind_name" +
                            "           , t2.code_value AS facility_kind_value" +
                            "      FROM t_facility t1" +
                            "           LEFT OUTER JOIN v_facility_kind t2 on t1.facility_kind = t2.code_seq" +
                            "      WHERE t2.code_value" + SqlUtil.getWhereInStr(facilityKind) +
                            "    ) t" +
                            "    WHERE t.station_seq IS NOT NULL" +
                            "    GROUP BY t.station_seq" +
                            ") t");
                }};

                SELECT("t4.in_facility_kind");
                LEFT_OUTER_JOIN("(" + innerSql.toString() + ") t4 on t1.station_seq = t4.station_seq");
                WHERE("t4.station_seq IS NOT NULL");
            }

            if (station != null && !station.isEmpty()) {
                WHERE("t2.code_value" + SqlUtil.getWhereInStr(station));
            }

//            현재는 데이터가 안맞아서 주석 해놓음
//            if (administZone != null && !administZone.isEmpty()) {
////                WHERE("t6.code_value" + SqlUtil.getWhereInStr(administZone));
//                WHERE("t6.emd_cd" + SqlUtil.getWhereInStr(administZone));
//            }

            if (keyword != null && !keyword.equals("")) {
                WHERE("(t1.station_name LIKE '%" + keyword + "%'" +
                        " OR t2.code_name LIKE '%" + keyword + "%'" +
//                        현재 뷰테이블과 시설물 구역이 맞지 않아 임시로 조회
//                        " OR t3.code_name LIKE '%" + keyword + "%'" +
                        " OR t3.emd_nm LIKE '%" + keyword + "%'" +
                        ")");
            }

            ORDER_BY("t1.station_seq");
            if (!start.equals("") && !length.equals("")) {
                LIMIT(length);
                OFFSET(start);
            }
        }};
        return sql.toString();
    }

    public String selectCountQry(Map<String, Object> paramMap) {
        String keyword = CommonUtil.validOneNull(paramMap, "keyword");
        ArrayList<String> facilityKind = CommonUtil.valiArrNull(paramMap, "facilityKind");
        ArrayList<String> administZone = CommonUtil.valiArrNull(paramMap, "administZone");
        ArrayList<String> station = CommonUtil.valiArrNull(paramMap, "station");

        SQL sql = new SQL() {{
            SELECT("COUNT(*) AS count");
            FROM("t_station t1");
            LEFT_OUTER_JOIN("v_facility_station t2 on t1.station_kind = t2.code_seq");
//            현재 뷰테이블과 시설물 구역이 맞지 않아 임시로 조회
//            LEFT_OUTER_JOIN("v_administ t3 on t1.administ_zone = t3.code_value");
            LEFT_OUTER_JOIN("t_area_emd t3 on t1.administ_zone = t3.emd_cd");

            if (facilityKind != null && !facilityKind.isEmpty()) {
                SQL innerSql = new SQL() {{
                    SELECT("station_seq" +
                            ", CASE WHEN cnt = 1 THEN in_facility_kind_name" +
                            " ELSE CONCAT(in_facility_kind_name, ' 외 ', CAST(cnt - 1 AS VARCHAR(20)), '종')" +
                            " END AS in_facility_kind");
                    FROM("(" +
                            "    SELECT t.station_seq" +
                            "      , MIN(t.facility_kind_name) AS in_facility_kind_name" +
                            "      , COUNT(*)                  AS cnt" +
                            "    FROM (" +
                            "      SELECT t1.station_seq" +
                            "           , t1.facility_kind" +
                            "           , t2.code_name  AS facility_kind_name" +
                            "           , t2.code_value AS facility_kind_value" +
                            "      FROM t_facility t1" +
                            "           LEFT OUTER JOIN v_facility_kind t2 on t1.facility_kind = t2.code_seq" +
                            "      WHERE t2.code_value" + SqlUtil.getWhereInStr(facilityKind) +
                            "    ) t" +
                            "    WHERE t.station_seq IS NOT NULL" +
                            "    GROUP BY t.station_seq" +
                            ") t");
                }};

                LEFT_OUTER_JOIN("(" + innerSql.toString() + ") t4 on t1.station_seq = t4.station_seq");
                WHERE("t4.station_seq IS NOT NULL");
            }

            if (station != null && !station.isEmpty()) {
                WHERE("t2.code_value" + SqlUtil.getWhereInStr(station));
            }

//            현재는 데이터가 안맞아서 주석 해놓음
//            if (administZone != null && !administZone.isEmpty()) {
////                WHERE("t6.code_value" + SqlUtil.getWhereInStr(administZone));
//                WHERE("t6.emd_cd" + SqlUtil.getWhereInStr(administZone));
//            }

            if (keyword != null && !keyword.equals("")) {
                WHERE("(t1.station_name LIKE '%" + keyword + "%'" +
                        " OR t2.code_name LIKE '%" + keyword + "%'" +
//                        현재 뷰테이블과 시설물 구역이 맞지 않아 임시로 조회
//                        " OR t3.code_name LIKE '%" + keyword + "%'" +
                        " OR t3.emd_nm LIKE '%" + keyword + "%'" +
                        ")");
            }
        }};

        return sql.toString();
    }

    public String selectOneQry(int stationSeq) {
        SQL sql = new SQL() {{
            SELECT("*");
            FROM("t_station");
            WHERE("station_seq=" + stationSeq);
        }};
        return sql.toString();
    }

    public String insertQry(Map<String, Object> paramMap) {
        Map<String, Object> qryMap = SqlUtil.getInsertValuesStr(paramMap);

        SQL sql = new SQL() {{
            INSERT_INTO("t_station");
            VALUES(qryMap.get("columns").toString(), qryMap.get("values").toString());
        }};
        return sql.toString();
    }

    public String updateQry(Map<String, Object> paramMap) {
        String stationSeq = paramMap.get("stationSeq").toString();

        SQL sql = new SQL() {{
            UPDATE("t_station");
            SET(SqlUtil.getMultiSetStr(paramMap));
            WHERE("station_seq = " + stationSeq);
        }};
        return sql.toString();
    }

    public String deleteQry(int seq) {
        SQL sql = new SQL() {{
            DELETE_FROM("t_station");
            WHERE("station_seq = " + seq);
        }};
        return sql.toString();
    }

    public String selectOneLastStationSeqQry() {
        SQL sql = new SQL() {
            {
                SELECT("currval('t_station_seq_seq') AS last_station_seq");
            }
        };
        return sql.toString();
    }

    public String selectListStationForSignageQry(Map<String, Object> paramMap) {

        SQL sql = new SQL() {{
            ArrayList stationSeqList = CommonUtil.valiArrNull(paramMap,"stationSeqList");

            SELECT("t1.station_seq, t1.station_name, t1.station_kind" +
                    ", t1.latitude, t1.longitude, t2.code_name AS station_kind_name," +
                    " t2.code_value AS station_kind_value, t3.emd_nm AS administ_zone_name");
            FROM("t_station t1");
            LEFT_OUTER_JOIN("v_facility_station t2 on t1.station_kind = t2.code_seq");
            LEFT_OUTER_JOIN("t_area_emd t3 on t1.administ_zone = t3.emd_cd");
            WHERE("t2.code_seq = 62 " );
            if(stationSeqList != null && !stationSeqList.isEmpty()) {
                WHERE("t1.station_seq NOT " + SqlUtil.getWhereInStr(stationSeqList));
            }
            ORDER_BY("t1.station_seq");
        }};
        return sql.toString();
    }

    public String selectOneStationForSignageQry(int seq) {
        SQL sql = new SQL() {
            {
                SELECT("t1.station_seq, t1.station_name, t1.station_kind" +
                        ", t1.latitude, t1.longitude, t2.code_name AS station_kind_name," +
                        " t2.code_value AS station_kind_value, t4.rtsp_url");
                FROM("t_station t1");
                LEFT_OUTER_JOIN("v_facility_station t2 on t1.station_kind = t2.code_seq");
                INNER_JOIN("(" +
                        "SELECT * " +
                        "FROM t_facility s1 " +
                        "INNER JOIN v_facility_rtsp s2 on s1.facility_seq = s2.facility_seq " +
                        "WHERE s1.facility_kind = 58 " +
                        ") t4 ON t1.station_seq = t4.station_seq");
                WHERE("t1.station_seq = "+ seq );
                WHERE("t2.code_seq = 62");
            }
        };
        return sql.toString();
    }
}
