package com.danusys.web.commons.auth.repository;


import com.danusys.web.commons.auth.model.User;
import com.danusys.web.commons.auth.model.UserDto;
import com.danusys.web.commons.auth.model.UserGroupInUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User,Long> {

    User findByUserId(String username);

    User findByUserSeq(int userSeq);

    Long deleteByUserSeq(int id);

    List<User> findAll();
}
