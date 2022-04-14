package com.danusys.web.commons.api.repository;

import com.danusys.web.commons.api.model.FacilityOpt;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Project : danusys-webservice-parent
 * Created by Intellij IDEA
 * Developer : ippo
 * Date : 2022/02/15
 * Time : 17:20
 */
public interface FacilityOptRepository extends JpaRepository<FacilityOpt, Long> {
//    List<FacilityOpt> findByFacilitySeq(Long facilitySeq);
}
