package com.danusys.web.commons.auth.session.repository;

import com.danusys.web.commons.auth.session.model.Permit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PermitRepository extends JpaRepository<Permit,Integer> {

    Permit findByCodeValue(String codeValue);

    Permit findByCodeSeq(int codeSeq);

}
