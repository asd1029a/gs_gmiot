package com.danusys.web.commons.auth.repository;

import com.danusys.web.commons.auth.model.Permit;
import com.danusys.web.commons.auth.model.UserGroup;
import com.danusys.web.commons.auth.model.UserGroupPermit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserGroupPermitRepository extends JpaRepository<UserGroupPermit,Integer> {

    UserGroupPermit findById(int id);

    Long deleteByUserGroup2AndPermit(UserGroup usergroup, Permit permit);


}
