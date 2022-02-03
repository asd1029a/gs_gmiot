package com.danusys.web.commons.auth.repository;

import com.danusys.web.commons.auth.model.Permit;
import com.danusys.web.commons.auth.model.UserGroup;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PermitRepository extends JpaRepository<Permit,Integer> {

    Permit findByPermitName(String permitName);

    Permit findByPermitSeq(int permitSeq);

}
