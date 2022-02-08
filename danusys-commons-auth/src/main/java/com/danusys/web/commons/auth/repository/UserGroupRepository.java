package com.danusys.web.commons.auth.repository;

import com.danusys.web.commons.auth.model.User;
import com.danusys.web.commons.auth.model.UserGroup;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserGroupRepository extends JpaRepository<UserGroup,Integer> {

    UserGroup findByGroupName(String groupName);

    UserGroup findByUserGroupSeq(int userGroupSeq);

}