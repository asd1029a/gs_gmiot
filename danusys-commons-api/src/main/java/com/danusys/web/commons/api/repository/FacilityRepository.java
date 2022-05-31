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

    List<Facility> findByFacilityKind(Long facilityKind);

    @Query(value = "SELECT fn_lonlat_to_emdcode(:longitude, :latitude)", nativeQuery = true)
    String getEmdCode(double longitude, double latitude);

}
