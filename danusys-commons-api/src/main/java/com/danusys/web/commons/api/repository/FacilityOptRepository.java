package com.danusys.web.commons.api.repository;

import com.danusys.web.commons.api.model.FacilityOpt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Project : danusys-webservice-parent
 * Created by Intellij IDEA
 * Developer : ippo
 * Date : 2022/02/15
 * Time : 17:20
 */
public interface FacilityOptRepository extends JpaRepository<FacilityOpt, Long> {

    FacilityOpt findByFacilitySeqAndFacilityOptName(Long facilitySeq, String facilityOptName);
    List<FacilityOpt> findAllByFacilitySeqAndFacilityOptName(Long facilitySeq, String facilityOptName);
    List<FacilityOpt> findByFacilitySeq(Long facilitySeq);
    FacilityOpt findByFacilityOptNameAndFacilityOptValue(String facilityOptName, String facilityOptValue);
    @Query(value = "SELECT * FROM t_facility_opt WHERE facility_opt_seq IN (SELECT MAX(facility_opt_seq) FROM t_facility_opt WHERE facility_seq = :facilitySeq GROUP BY facility_opt_name)", nativeQuery = true)
    List<FacilityOpt> findByFacilitySeqLast(@Param("facilitySeq") Long facilitySeq);
}
