package com.danusys.web.commons.auth.session.repository;


import com.danusys.web.commons.auth.session.model.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserStatusRepository extends JpaRepository<UserStatus,Long>  {

    UserStatus findByCodeValue(String codeValue);






}
