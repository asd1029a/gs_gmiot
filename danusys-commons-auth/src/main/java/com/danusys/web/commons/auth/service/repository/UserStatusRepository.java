package com.danusys.web.commons.auth.service.repository;


import com.danusys.web.commons.auth.model.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserStatusRepository extends JpaRepository<UserStatus,Long>  {

    UserStatus findByCodeValue(String codeValue);






}
