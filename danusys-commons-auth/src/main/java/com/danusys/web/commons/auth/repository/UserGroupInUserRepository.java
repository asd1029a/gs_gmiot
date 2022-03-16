package com.danusys.web.commons.auth.repository;

import com.danusys.web.commons.auth.model.User;
import com.danusys.web.commons.auth.model.UserGroup;
import com.danusys.web.commons.auth.model.UserGroupInUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserGroupInUserRepository extends JpaRepository<UserGroupInUser,Integer> {

    UserGroupInUser findByUserGroup(int userGroupSeq);

    UserGroupInUser findByUser(User user);

    List<UserGroupInUser> findAllByUser(User user);

    List<UserGroupInUser> findAllByUserGroup(UserGroup userGroup);

    Long deleteByUserGroup(UserGroup userGroup);

    Long deleteByUserAndUserGroup(User user, UserGroup userGroup);

    List<UserGroupInUser> findByUserAndUserGroup(User user, UserGroup userGroup);

}
