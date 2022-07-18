package com.danusys.web.commons.api.repository;

import com.danusys.web.commons.api.model.Facility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Project : danusys-webservice-parent
 * Created by Intellij IDEA
 * Developer : ippo
 * Date : 2022/02/08
 * Time : 16:06
 */
public interface FacilityRepository extends JpaRepository<Facility, Long> {
    List<Facility> findAll();

    Facility findByFacilityId(String facilityId);

    @Query(value = "SELECT code_seq FROM v_facility_kind WHERE code_id = :codeId", nativeQuery = true)
    Long findCommonCode(String codeId);

    @Query(value = "SELECT code_name FROM v_facility_kind WHERE code_seq = :code_seq", nativeQuery = true)
    String findCommonCodeName(Long code_seq);

    List<Facility> findByFacilityKind(Long facilityKind);

    @Query(value = "SELECT fn_lonlat_to_emdcode(:longitude, :latitude)", nativeQuery = true)
    String getEmdCode(double longitude, double latitude);

    List<Facility> findByStationSeq(Long stationSeq);

    @Query(value = "SELECT code_seq FROM v_facility_kind WHERE code_value IN (:facilityKindValues)", nativeQuery = true)
    List<Long> findFacilityKindList(List<String> facilityKindValues);

    @Query(value = "SELECT * FROM t_facility WHERE administ_zone like :administZone% AND facility_kind IN (:facilityKind)", nativeQuery = true)
    List<Facility> findByAdministZoneAndFacilityKindIn(String administZone, List<Long> facilityKind);

    List<Facility> findByFacilityKindAndLatitudeAndLongitude(Long facilityKind, double latitude, double longitude);

    @Query(value=
            "select t2.* from ( " +
                "select t1.*, row_number() over (order by t1.distance asc) as rnum from ( " +
                    " select t0.*, " +
                    " ST_DISTANCE( " +
                        " CAST((select st_geomfromtext(concat('point(', t0.longitude, ' ', t0.latitude, ')'),4326)) AS geography), "+
                        " CAST((select st_geomfromtext(concat('point(', :longitude, ' ', :latitude, ')'), 4326)) AS geography) " +
                    " ) as distance from t_facility t0 " +
                    " inner join v_facility_kind v1 on t0.facility_kind = v1.code_seq " +
                    " inner join t_facility_opt tp on t0.facility_seq = tp.facility_seq " +
                    " where v1.code_value = 'CCTV' " +
                        " and (tp.facility_opt_name = 'cctv_head' and tp.facility_opt_value = '1') " +
                        " and t0.station_seq is null " +
                        //" and and administ_zone like " + administZone + "%' "); " +
                " ) t1 where 0 < distance and distance < 500 " +
            " ) t2 where rnum < 6 "
    , nativeQuery = true)
    List<Facility> findByGeomSql(double latitude, double longitude);


}
