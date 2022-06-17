package com.danusys.web.commons.api.repository;

import com.danusys.web.commons.api.model.FacilityActiveLog;
import org.springframework.data.jpa.repository.JpaRepository;


public interface FacilityActiveRepository extends JpaRepository<FacilityActiveLog,Long> {
}
