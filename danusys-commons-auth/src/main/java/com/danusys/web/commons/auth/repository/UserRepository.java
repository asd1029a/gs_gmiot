package com.danusys.web.commons.auth.repository;


import com.danusys.web.commons.auth.model.User;
import com.danusys.web.commons.auth.model.UserGroupInUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Long> {

    User findByUserId(String userId);

    User findById(int userSeq);

}
