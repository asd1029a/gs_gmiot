package com.danusys.web.commons.api.repository;

import com.danusys.web.commons.api.model.FacilitySetting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FacilitySettingRepository extends JpaRepository<FacilitySetting, Long> {

    List<FacilitySetting> findAllByFacilitySeqOrderByFacilitySettingTime(Long facilitySeq);

    List<FacilitySetting> findAllByFacilitySettingTime(String facilitySettingTime);

}
