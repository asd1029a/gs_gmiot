package com.danusys.web.commons.auth.service.repository;

import com.danusys.web.commons.auth.model.Permit;
import com.danusys.web.commons.auth.model.UserGroup;
import com.danusys.web.commons.auth.model.UserGroupPermit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface UserGroupPermitRepository extends JpaRepository<UserGroupPermit,Integer> {

    UserGroupPermit findById(int id);

    Long deleteByUserGroupSeq(int userGroupSeq);
    Long deleteByUserGroup2AndPermit(UserGroup usergroup, Permit permit);

    @Modifying
    @Query("delete from UserGroupPermit where userGroup2.userGroupSeq = :userGroupSeq")
    void deleteAllByUserGroupSeq(int userGroupSeq);
}
