package com.danusys.web.commons.auth.session.repository;

import com.danusys.web.commons.auth.session.model.UserGroup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface UserGroupRepository extends JpaRepository<UserGroup,Integer>, JpaSpecificationExecutor<UserGroup> {

    UserGroup findByGroupName(String groupName);

    UserGroup findByUserGroupSeq(int userGroupSeq);

    Page<UserGroup> findByGroupNameIgnoreCaseLikeAndGroupDescIgnoreCaseLike(String groupName, String groupDesc, Pageable pageable);
}
