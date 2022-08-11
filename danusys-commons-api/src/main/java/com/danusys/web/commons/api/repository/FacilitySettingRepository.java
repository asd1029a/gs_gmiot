package com.danusys.web.commons.api.repository;

import com.danusys.web.commons.api.model.FacilitySetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;

public interface FacilitySettingRepository extends JpaRepository<FacilitySetting, Long> {

    void deleteAllByFacilitySeq(Long facilitySeq);

    List<FacilitySetting> findAllByFacilitySeqOrderByFacilitySettingTime(Long facilitySeq);

    List<FacilitySetting> findAllByFacilitySettingTime(String facilitySettingTime);

    List<FacilitySetting> findAllByFacilitySeqAndFacilitySettingTimeAndFacilitySettingDayOrderByFacilitySettingTime(Long facilitySeq, String facilitySettingTime, String facilitySettingDay);

    @Query(value=
            "select facility_id, facility_setting_time, facility_setting_day, ARRAY_TO_STRING(ARRAY_AGG(facility_setting_name),',') AS optionKey, ARRAY_TO_STRING(ARRAY_AGG(facility_setting_value),',') AS optionValue " +
                    "FROM t_facility_setting " +
                    "GROUP BY facility_setting_time, facility_id, facility_setting_day " +
                    "ORDER BY facility_setting_time ", nativeQuery = true
    )
    List<Map<String,Object>> findBySetScheduler();

    @Query(value = "select facility_key from t_facility_key_mapping where facility_seq = :facilitySeq and remarks = :remarks", nativeQuery = true)
    String findFacilityId(@Param("facilitySeq") Long facilitySeq, @Param("remarks") String remarks);


}
