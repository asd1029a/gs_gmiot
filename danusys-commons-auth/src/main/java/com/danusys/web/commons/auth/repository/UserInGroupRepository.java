package com.danusys.web.commons.auth.repository;

import com.danusys.web.commons.auth.model.User;
import com.danusys.web.commons.auth.model.UserGroup;
import com.danusys.web.commons.auth.model.UserInGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface UserInGroupRepository extends JpaRepository<UserInGroup,Integer> {

    UserInGroup findByUserGroup(int userGroupSeq);

    UserInGroup findByUser(User user);

    List<UserInGroup> findAllByUser(User user);

    List<UserInGroup> findAllByUserGroup(UserGroup userGroup);

    Long deleteByUserGroup(UserGroup userGroup);

    @Transactional
    @Modifying
    @Query("delete from UserInGroup where user.userSeq = :userSeq")
    void deleteAllByUserSeq(int userSeq);

    @Transactional
    @Modifying
    @Query("delete from UserInGroup where userGroup.userGroupSeq = :groupSeq")
    void deleteAllByUserGroupSeq(int groupSeq);

    Long deleteAllByUser(User user);

    Long deleteByUserAndUserGroup(User user, UserGroup userGroup);

    List<UserInGroup> findByUserAndUserGroup(User user, UserGroup userGroup);

}
