package com.danusys.web.commons.auth.service.repository;


import com.danusys.web.commons.auth.model.User;
import com.danusys.web.commons.auth.model.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserStatusRepository extends JpaRepository<UserStatus,Long>  {

    UserStatus findByCodeValue(String codeValue);






}
