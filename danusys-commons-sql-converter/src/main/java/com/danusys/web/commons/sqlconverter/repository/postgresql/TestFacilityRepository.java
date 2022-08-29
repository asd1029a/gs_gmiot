package com.danusys.web.commons.sqlconverter.repository.postgresql;

import com.danusys.web.commons.sqlconverter.model.mariadb.ErssEmerhydP;
import com.danusys.web.commons.sqlconverter.model.postgresql.TestFacility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TestFacilityRepository extends JpaRepository<TestFacility, Long> {

}
