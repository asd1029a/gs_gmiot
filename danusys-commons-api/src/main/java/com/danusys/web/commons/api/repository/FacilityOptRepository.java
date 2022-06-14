package com.danusys.web.commons.api.repository;

import com.danusys.web.commons.api.model.FacilityOpt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

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
    @Query(value = "SELECT t1.*, v1.code_value as facility_opt_type_name FROM t_facility_opt t1 " +
            "JOIN v_facility_opt_type v1 ON t1.facility_opt_type = v1.code_seq " +
            "WHERE t1.facility_seq = :facilitySeq", nativeQuery = true)
    List<FacilityOpt> findByFacilitySeq(Long facilitySeq);
    FacilityOpt findByFacilityOptNameAndFacilityOptValue(String facilityOptName, String facilityOptValue);
}
