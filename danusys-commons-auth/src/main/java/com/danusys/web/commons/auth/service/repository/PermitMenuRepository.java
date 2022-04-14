package com.danusys.web.commons.auth.service.repository;

import com.danusys.web.commons.auth.model.PermitMenu;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PermitMenuRepository extends JpaRepository<PermitMenu,Integer> {

    PermitMenu findByCodeValue(String codeValue);

    PermitMenu findByCodeSeq(int codeSeq);

}
