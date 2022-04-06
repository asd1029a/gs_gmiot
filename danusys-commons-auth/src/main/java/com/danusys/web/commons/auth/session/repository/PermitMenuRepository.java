package com.danusys.web.commons.auth.session.repository;

import com.danusys.web.commons.auth.session.model.PermitMenu;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PermitMenuRepository extends JpaRepository<PermitMenu,Integer> {

    PermitMenu findByCodeValue(String codeValue);

    PermitMenu findByCodeSeq(int codeSeq);

}
