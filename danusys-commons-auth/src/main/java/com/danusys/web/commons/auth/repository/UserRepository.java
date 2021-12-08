package com.danusys.web.commons.auth.repository;


import com.danusys.web.commons.auth.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Long> {

    User findByUsername(String username);

}
