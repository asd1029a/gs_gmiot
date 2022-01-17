package com.danusys.web.commons.auth.repository;

import com.danusys.web.commons.auth.model.User;
import com.danusys.web.commons.auth.model.UserGroup;
import com.danusys.web.commons.auth.model.UserGroupInUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserGroupInUserRepository extends JpaRepository<UserGroupInUser,Integer> {

    UserGroupInUser findByUserGroup(int userGroupSeq);

    UserGroupInUser findByUser(User user);


}
