package com.danusys.web.commons.auth.session.repository;

import com.danusys.web.commons.auth.session.model.Permit;
import com.danusys.web.commons.auth.session.model.UserGroup;
import com.danusys.web.commons.auth.session.model.UserGroupPermit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserGroupPermitRepository extends JpaRepository<UserGroupPermit,Integer> {

    UserGroupPermit findById(int id);

    Long deleteByUserGroup2AndPermit(UserGroup usergroup, Permit permit);


}
