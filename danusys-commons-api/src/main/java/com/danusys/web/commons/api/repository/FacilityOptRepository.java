package com.danusys.web.commons.api.repository;

import com.danusys.web.commons.api.model.FacilityOpt;
import org.springframework.data.jpa.repository.JpaRepository;

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
    List<FacilityOpt> findByFacilitySeq(Long facilitySeq);

    FacilityOpt findByFacilityOptNameAndFacilityOptValue(String facilityOptName, String facilityOptValue);
}
